package com.example.filmwatch.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [WatchlistFilmEntity::class],
    version = 11,
    exportSchema = false
)
abstract class FilmDatabase : RoomDatabase() {
    abstract fun filmDao(): FilmDao
}
