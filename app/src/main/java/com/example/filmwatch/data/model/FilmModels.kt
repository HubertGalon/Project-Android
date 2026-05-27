package com.example.filmwatch.data.model

data class SearchFilm(
    val imdbId: String,
    val title: String,
    val year: String,
    val posterUrl: String,
    val rating: String = ""
)

data class FilmDetails(
    val imdbId: String,
    val title: String,
    val year: String,
    val genre: String,
    val director: String,
    val plot: String,
    val imdbRating: String,
    val posterUrl: String
)
