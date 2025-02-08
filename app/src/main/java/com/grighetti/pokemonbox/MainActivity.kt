package com.grighetti.pokemonbox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.grighetti.pokemonbox.ui.screens.PokemonDetailScreen
import com.grighetti.pokemonbox.ui.screens.PokemonSearchScreen
import com.grighetti.pokemonbox.viewmodel.PokemonViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: PokemonViewModel = hiltViewModel() // ✅ ViewModel creato qui
            PokemonApp(viewModel)
        }
    }
}

sealed class Screen(val route: String) {
    data object Search : Screen("search")
    data object Detail : Screen("detail/{pokemonName}") {
        fun createRoute(pokemonName: String) = "detail/$pokemonName"
    }
}

@Composable
fun PokemonApp(viewModel: PokemonViewModel) { // ✅ Ora lo passiamo come parametro
    val navController = rememberNavController()

    NavHost(navController, startDestination = Screen.Search.route) {
        composable(Screen.Search.route) {
            PokemonSearchScreen(navController, viewModel)  // ✅ Passiamo il ViewModel
        }
        composable(
            Screen.Detail.route,
            arguments = listOf(navArgument("pokemonName") { type = NavType.StringType })
        ) { backStackEntry ->
            val pokemonName = backStackEntry.arguments?.getString("pokemonName") ?: ""
            PokemonDetailScreen(navController, pokemonName, viewModel) // ✅ Passato anche qui
        }
    }
}

fun navigateToDetail(navController: NavController, searchQuery: String) {
    if (searchQuery.isNotBlank()) {
        navController.navigate(Screen.Detail.createRoute(searchQuery.lowercase()))
    }
}
