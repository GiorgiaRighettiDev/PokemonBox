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

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokemonApp() // Initialize the main Compose UI
        }
    }
}

/**
 * Defines app screens and their navigation routes.
 */
sealed class Screen(val route: String) {
    data object Search : Screen("search") // Search screen route
    data object Detail : Screen("detail/{pokemonName}") {
        fun createRoute(pokemonName: String) = "detail/${pokemonName.lowercase()}"
    }
}

/**
 * Sets up navigation for the app using Jetpack Compose Navigation.
 */
@Composable
fun PokemonApp() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = Screen.Search.route) {
        composable(Screen.Search.route) {
            PokemonSearchScreen(navController) // Displays the search screen
        }
        composable(
            Screen.Detail.route,
            arguments = listOf(navArgument("pokemonName") { type = NavType.StringType })
        ) { backStackEntry ->
            val pokemonName = backStackEntry.arguments?.getString("pokemonName") ?: ""
            PokemonDetailScreen(navController, pokemonName) // Displays the Pokémon details screen
        }
    }
}

/**
 * Navigates to the Pokémon detail screen with the given search query.
 * Ensures navigation state is preserved and avoids duplicate entries in the back stack.
 */
fun NavController.navigateToDetail(searchQuery: String) {
    if (searchQuery.isNotBlank()) {
        this.navigate(Screen.Detail.createRoute(searchQuery)) {
            popUpTo(Screen.Search.route) { saveState = true } // Retains previous state when navigating back
            launchSingleTop = true // Avoids multiple instances of the same screen
            restoreState = true // Restores the previous UI state
        }
    }
}
