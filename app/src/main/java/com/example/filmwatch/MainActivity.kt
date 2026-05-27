package com.example.filmwatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.filmwatch.ui.navigation.AppNavGraph
import com.example.filmwatch.ui.theme.FilmWatchTheme
import com.example.filmwatch.ui.viewmodel.FilmViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as FilmWatchApp
        val factory = FilmViewModelFactory(app.repository)

        setContent {
            FilmWatchTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavGraph(factory = factory)
                }
            }
        }
    }
}
