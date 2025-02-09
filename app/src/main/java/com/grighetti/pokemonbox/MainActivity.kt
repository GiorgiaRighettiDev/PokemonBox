package com.grighetti.pokemonbox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.grighetti.pokemonbox.ui.screens.PokemonDetailScreen
import com.grighetti.pokemonbox.ui.screens.PokemonSearchScreen
import dagger.hilt.android.AndroidEntryPoint

/**
 * Entry point of the application.
 * Hosts the main UI and initializes the Compose navigation.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokemonApp()
        }
    }
}

/**
 * Defines the app's navigation routes.
 */
sealed class Screen(val route: String) {
    data object Search : Screen("search")

    data object Detail : Screen("detail/{pokemonName}") {
        /**
         * Constructs a navigation route for the detail screen with a given Pokémon name.
         * Ensures that names are always lowercase for consistency.
         *
         * @param pokemonName The name of the Pokémon.
         * @return A formatted navigation route string.
         */
        fun createRoute(pokemonName: String) = "detail/${pokemonName.lowercase()}"
    }
}

/**
 * Sets up the navigation system for the app using Jetpack Compose Navigation.
 * Manages transitions between the search and detail screens.
 */
@Composable
fun PokemonApp() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = Screen.Search.route) {
        composable(Screen.Search.route) {
            PokemonSearchScreen(navController)
        }
        composable(
            Screen.Detail.route,
            arguments = listOf(navArgument("pokemonName") { type = NavType.StringType })
        ) { backStackEntry ->
            val pokemonName = backStackEntry.arguments?.getString("pokemonName") ?: ""
            PokemonDetailScreen(navController, pokemonName)
        }
    }
}

/**
 * Navigates to the Pokémon detail screen with a given search query.
 * Ensures proper navigation handling to avoid duplicate entries in the back stack.
 *
 * @param searchQuery The Pokémon name to navigate to.
 */
fun NavController.navigateToDetail(searchQuery: String) {
    if (searchQuery.isNotBlank()) {
        this.navigate(Screen.Detail.createRoute(searchQuery)) {
            popUpTo(Screen.Search.route) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }
}
