package com.example.filmwatch.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist_films")
data class WatchlistFilmEntity(
    @PrimaryKey val imdbId: String,
    val title: String,
    val year: String,
    val genre: String,
    val director: String,
    val plot: String,
    val imdbRating: String,
    val posterUrl: String,
    val isWatched: Boolean = false,
    val addedAt: Long = System.currentTimeMillis()
)
