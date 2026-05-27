package com.example.filmwatch.data.repository

import com.example.filmwatch.BuildConfig
import com.example.filmwatch.data.local.FilmDao
import com.example.filmwatch.data.local.WatchlistFilmEntity
import com.example.filmwatch.data.model.FilmDetails
import com.example.filmwatch.data.model.SearchFilm
import com.example.filmwatch.data.remote.OmdbApi
import kotlinx.coroutines.flow.Flow

class FilmRepository(
    private val dao: FilmDao,
    private val api: OmdbApi
) {
    fun observeWatchlist(): Flow<List<WatchlistFilmEntity>> = dao.getAllFilms()

    suspend fun searchFilms(query: String): Result<List<SearchFilm>> = runCatching {
        val apiKey = BuildConfig.OMDB_API_KEY
        require(apiKey.isNotBlank()) { "Brak klucza OMDb API." }

        val response = api.searchFilms(apiKey = apiKey, query = query)
        if (response.response == "False") {
            throw IllegalStateException(response.error ?: "Błąd pobierania listy.")
        }

        response.search.orEmpty().map {
            SearchFilm(
                imdbId = it.imdbId,
                title = it.title,
                year = it.year,
                posterUrl = it.poster.takeIf { url -> url != "N/A" }.orEmpty(),
                rating = ""
            )
        }
    }

    suspend fun getFilmsByIds(ids: List<String>): Result<List<SearchFilm>> = runCatching {
        ids.mapNotNull { id ->
            getFilmDetails(id).getOrNull()?.let { details ->
                SearchFilm(
                    imdbId = details.imdbId,
                    title = details.title,
                    year = details.year,
                    posterUrl = details.posterUrl,
                    rating = details.imdbRating
                )
            }
        }
    }

    suspend fun getFilmDetails(imdbId: String): Result<FilmDetails> = runCatching {
        val apiKey = BuildConfig.OMDB_API_KEY
        val response = api.getFilmDetails(apiKey = apiKey, imdbId = imdbId)
        if (response.response == "False") {
            throw IllegalStateException(response.error ?: "Błąd szczegółów.")
        }

        FilmDetails(
            imdbId = response.imdbId.orEmpty(),
            title = response.title.orEmpty(),
            year = response.year.orEmpty(),
            genre = response.genre.orEmpty(),
            director = response.director.orEmpty(),
            plot = response.plot.orEmpty(),
            imdbRating = response.imdbRating.orEmpty(),
            posterUrl = response.poster.takeIf { it != null && it != "N/A" }.orEmpty()
        )
    }

    suspend fun addToWatchlist(details: FilmDetails) {
        dao.insertFilm(
            WatchlistFilmEntity(
                imdbId = details.imdbId,
                title = details.title,
                year = details.year,
                genre = details.genre,
                director = details.director,
                plot = details.plot,
                imdbRating = details.imdbRating,
                posterUrl = details.posterUrl
            )
        )
    }

    suspend fun removeFromWatchlist(imdbId: String) = dao.deleteById(imdbId)
    suspend fun updateWatchedStatus(imdbId: String, isWatched: Boolean) = dao.updateWatchedStatus(imdbId, isWatched)
    suspend fun isInWatchlist(imdbId: String): Boolean = dao.exists(imdbId)
}
