package com.example.filmwatch.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filmwatch.data.local.WatchlistFilmEntity
import com.example.filmwatch.data.model.FilmDetails
import com.example.filmwatch.data.model.SearchFilm
import com.example.filmwatch.data.repository.FilmRepository
import com.example.filmwatch.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchScreenState(
    val query: String = "",
    val searchResults: UiState<List<SearchFilm>> = UiState.Idle,
    val exploreResults: UiState<List<SearchFilm>> = UiState.Idle,
    val selectedFilm: UiState<FilmDetails> = UiState.Idle,
    val isSelectedInWatchlist: Boolean = false,
    val message: String? = null,
    val currentExploreCategory: String? = null
)

class FilmViewModel(
    private val repository: FilmRepository
) : ViewModel() {
    private val _searchState = MutableStateFlow(SearchScreenState())
    val searchState: StateFlow<SearchScreenState> = _searchState.asStateFlow()

    private val _selectedGenre = MutableStateFlow("Wszystkie")
    val selectedGenre: StateFlow<String> = _selectedGenre.asStateFlow()

    private val curatedMovies = mapOf(
        "Action" to listOf("tt0468569", "tt1375666", "tt0133093", "tt1392190", "tt2407311"),
        "Comedy" to listOf("tt0088763", "tt1119646", "tt0485947", "tt0087332", "tt0107048"),
        "Horror" to listOf("tt0081505", "tt0078748", "tt0060192", "tt1457767", "tt0070047"),
        "Sci-Fi" to listOf("tt0816692", "tt0076759", "tt1856101", "tt2543428", "tt1160419"),
        "Drama" to listOf("tt0111161", "tt0068646", "tt0109830", "tt6751668", "tt0050083"),
        "Thriller" to listOf("tt0114369", "tt0102926", "tt1130884", "tt7026488", "tt0209144"),
        "Animation" to listOf("tt4633694", "tt0245429", "tt0110413", "tt0114709", "tt0910970")
    )

    private val massiveLuckyPool = listOf(
        // Klasyki i Dramaty
        "tt0111161", "tt0068646", "tt0068646", "tt0110912", "tt0080684", "tt0109830", "tt0062622", "tt0167260",
        "tt0167261", "tt0167262", "tt0133093", "tt0071562", "tt0114709", "tt0081505", "tt0102926", "tt0073486",
        "tt0050083", "tt0120737", "tt0095765", "tt0114369", "tt0082971", "tt0031381", "tt0054215", "tt0114814",
        // Akcja i Sci-Fi
        "tt0468569", "tt1375666", "tt0816692", "tt0137523", "tt1856101", "tt2543428", "tt0076759", "tt1130884",
        "tt1392190", "tt0088763", "tt0087332", "tt0108052", "tt0120689", "tt0120800", "tt0338526", "tt0172495",
        "tt2096673", "tt2380307", "tt0099685", "tt0407887", "tt0451279", "tt0478970", "tt1431045", "tt1677720",
        // Thriller / Kryminał
        "tt0102138", "tt0101414", "tt0103064", "tt0209144", "tt0095016", "tt0096283", "tt0311448", "tt0371746",
        "tt0482571", "tt0944835", "tt0993846", "tt1300854", "tt7026488", "tt6751668", "tt0110357", "tt0112130",
        // Komedia i Animacja
        "tt1119646", "tt0485947", "tt0107048", "tt4633694", "tt0245429", "tt0110413", "tt0910970", "tt0120338",
        "tt0361748", "tt0482571", "tt0816692", "tt1160419", "tt1454468", "tt1396484", "tt1457767", "tt0078748"
    ).distinct()

    val exploreCategories = curatedMovies.keys.toList()

    val watchlist: StateFlow<List<WatchlistFilmEntity>> = repository
        .observeWatchlist()
        .combine(_selectedGenre) { films, genre ->
            if (genre == "Wszystkie") films 
            else films.filter { it.genre.contains(genre, ignoreCase = true) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val genres: StateFlow<List<String>> = repository
        .observeWatchlist()
        .map { films -> 
            val allGenres = films.flatMap { it.genre.split(", ") }
                .filter { it.isNotBlank() && it != "N/A" }
                .distinct()
                .sorted()
            listOf("Wszystkie") + allGenres
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), listOf("Wszystkie"))

    init {
        exploreByCategory("Action")
    }

    fun updateQuery(newQuery: String) {
        _searchState.update { it.copy(query = newQuery) }
        if (newQuery.isBlank()) {
            _searchState.update { it.copy(searchResults = UiState.Idle) }
        }
    }

    fun selectGenre(genre: String) {
        _selectedGenre.value = genre
    }

    fun clearMessage() {
        _searchState.update { it.copy(message = null) }
    }

    fun searchFilms() {
        val query = searchState.value.query.trim()
        if (query.isBlank()) return

        viewModelScope.launch {
            _searchState.update { it.copy(searchResults = UiState.Loading) }
            val result = repository.searchFilms(query)
            _searchState.update {
                result.fold(
                    onSuccess = { films -> it.copy(searchResults = UiState.Success(films)) },
                    onFailure = { error -> it.copy(searchResults = UiState.Error(error.message ?: "Błąd wyszukiwania")) }
                )
            }
        }
    }

    fun exploreByCategory(category: String) {
        val ids = curatedMovies[category] ?: return
        viewModelScope.launch {
            _searchState.update { it.copy(exploreResults = UiState.Loading, currentExploreCategory = category) }
            val result = repository.getFilmsByIds(ids)
            _searchState.update {
                result.fold(
                    onSuccess = { films -> it.copy(exploreResults = UiState.Success(films)) },
                    onFailure = { error -> it.copy(exploreResults = UiState.Error(error.message ?: "Błąd kategorii")) }
                )
            }
        }
    }

    fun getRandomMovieId(): String = massiveLuckyPool.random()

    fun loadFilmDetails(imdbId: String) {
        viewModelScope.launch {
            _searchState.update { it.copy(selectedFilm = UiState.Loading) }
            val result = repository.getFilmDetails(imdbId)
            val isSaved = repository.isInWatchlist(imdbId)
            _searchState.update {
                result.fold(
                    onSuccess = { details -> 
                        it.copy(selectedFilm = UiState.Success(details), isSelectedInWatchlist = isSaved) 
                    },
                    onFailure = { error -> 
                        it.copy(selectedFilm = UiState.Error(error.message ?: "Nieznany błąd")) 
                    }
                )
            }
        }
    }

    fun addSelectedFilmToWatchlist() {
        val details = (searchState.value.selectedFilm as? UiState.Success)?.data ?: return
        viewModelScope.launch {
            repository.addToWatchlist(details)
            _searchState.update { it.copy(isSelectedInWatchlist = true, message = "Film dodany do listy.") }
        }
    }

    fun removeSelectedFilmFromWatchlist() {
        val details = (searchState.value.selectedFilm as? UiState.Success)?.data ?: return
        viewModelScope.launch {
            repository.removeFromWatchlist(details.imdbId)
            _searchState.update { it.copy(isSelectedInWatchlist = false, message = "Film usunięty z listy.") }
        }
    }

    fun removeFromWatchlist(imdbId: String) {
        viewModelScope.launch { repository.removeFromWatchlist(imdbId) }
    }

    fun toggleWatchedStatus(imdbId: String, currentStatus: Boolean) {
        viewModelScope.launch { repository.updateWatchedStatus(imdbId, !currentStatus) }
    }
}
