package com.example.filmwatch.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.filmwatch.ui.screens.FilmDetailsScreen
import com.example.filmwatch.ui.screens.SearchScreen
import com.example.filmwatch.ui.screens.WatchlistScreen
import com.example.filmwatch.ui.viewmodel.FilmViewModel
import com.example.filmwatch.ui.viewmodel.FilmViewModelFactory

sealed class Screen(val route: String, val label: String) {
    data object Search : Screen("search", "Szukaj")
    data object Watchlist : Screen("watchlist", "Moja lista")

    data object Details : Screen("details/{imdbId}", "Szczegóły") {
        fun createRoute(imdbId: String) = "details/$imdbId"
    }
}

@Composable
fun AppNavGraph(factory: FilmViewModelFactory) {
    val navController = rememberNavController()
    val viewModel: FilmViewModel = viewModel(factory = factory)
    val items = listOf(Screen.Search, Screen.Watchlist)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { screen ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (screen.route == Screen.Search.route) Icons.Default.Search else Icons.Default.List,
                                contentDescription = screen.label
                            )
                        },
                        label = { Text(screen.label) }
                    )
                }

            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Search.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Search.route) {
                SearchScreen(
                    viewModel = viewModel,
                    onFilmClick = { imdbId ->
                        navController.navigate(Screen.Details.createRoute(imdbId))
                    }
                )
            }

            composable(Screen.Watchlist.route) {
                WatchlistScreen(
                    viewModel = viewModel,
                    onFilmClick = { imdbId ->
                        navController.navigate(Screen.Details.createRoute(imdbId))
                    }
                )
            }

            composable(Screen.Details.route) { backStackEntry ->
                val imdbId = backStackEntry.arguments?.getString("imdbId").orEmpty()
                FilmDetailsScreen(
                    imdbId = imdbId,
                    viewModel = viewModel,
                    onBack = {
                        if (navController.previousBackStackEntry != null) {
                            navController.popBackStack()
                        } else {
                            navController.navigate(Screen.Search.route) {
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
        }
    }
}
