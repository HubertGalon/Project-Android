package com.example.filmwatch

import android.app.Application
import androidx.room.Room
import com.example.filmwatch.data.local.FilmDatabase
import com.example.filmwatch.data.remote.RetrofitProvider
import com.example.filmwatch.data.repository.FilmRepository

class FilmWatchApp : Application() {
    lateinit var repository: FilmRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val db = Room.databaseBuilder(
            applicationContext,
            FilmDatabase::class.java,
            "film_watch_db"
        ).fallbackToDestructiveMigration().build()

        repository = FilmRepository(
            dao = db.filmDao(),
            api = RetrofitProvider.omdbApi
        )
    }
}
