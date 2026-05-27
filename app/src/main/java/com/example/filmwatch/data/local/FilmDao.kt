package com.example.filmwatch.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FilmDao {
    @Query("SELECT * FROM watchlist_films ORDER BY addedAt DESC")
    fun getAllFilms(): Flow<List<WatchlistFilmEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM watchlist_films WHERE imdbId = :imdbId)")
    suspend fun exists(imdbId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFilm(film: WatchlistFilmEntity)

    @Query("DELETE FROM watchlist_films WHERE imdbId = :imdbId")
    suspend fun deleteById(imdbId: String)

    @Query("UPDATE watchlist_films SET isWatched = :isWatched WHERE imdbId = :imdbId")
    suspend fun updateWatchedStatus(imdbId: String, isWatched: Boolean)
}
