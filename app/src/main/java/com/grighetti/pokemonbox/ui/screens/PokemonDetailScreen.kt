package com.grighetti.pokemonbox.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.grighetti.pokemonbox.data.domain.PokemonDetail
import com.grighetti.pokemonbox.ui.TypeBadge
import com.grighetti.pokemonbox.ui.pokemondetail.PokemonTabs
import com.grighetti.pokemonbox.ui.theme.DefaultLarge
import com.grighetti.pokemonbox.ui.theme.DefaultMedium
import com.grighetti.pokemonbox.ui.theme.DefaultSmall
import com.grighetti.pokemonbox.ui.theme.ErrorColor
import com.grighetti.pokemonbox.ui.theme.TextHighlight
import com.grighetti.pokemonbox.ui.theme.TextPrimary
import com.grighetti.pokemonbox.ui.theme.Typography
import com.grighetti.pokemonbox.viewmodel.PokemonViewModel


/**
 * Screen displaying Pokémon details, including stats, evolution chain, and additional information.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailScreen(
    navController: NavController,
    pokemonName: String,
    viewModel: PokemonViewModel = hiltViewModel()
) {
    val uiState by viewModel.pokemonDetailUiState.collectAsState()

    LaunchedEffect(pokemonName) {
        viewModel.searchPokemonDetail(pokemonName)
    }

    Scaffold(
        modifier = Modifier.background(Color.White),
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = TextPrimary,
                    navigationIconContentColor = TextPrimary
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(DefaultLarge, innerPadding.calculateTopPadding(), DefaultLarge, 0.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> CircularProgressIndicator()
                uiState.errorMessage.isNotEmpty() -> Text(uiState.errorMessage, color = ErrorColor)
                uiState.pokemon != null -> PokemonDetailContent(uiState.pokemon!!)
            }
        }
    }
}

/**
 * Displays the Pokémon details with a header and tabs for additional information.
 */
@Composable
fun PokemonDetailContent(pokemon: PokemonDetail) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(DefaultLarge)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(0.65f),
                    verticalArrangement = Arrangement.spacedBy(DefaultSmall)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(DefaultMedium),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = pokemon.name.replaceFirstChar { it.uppercase() },
                            style = Typography.headlineMedium
                        )
                        Text(
                            "#${pokemon.nationalDexNumber.toString().padStart(3, '0')}",
                            color = TextHighlight
                        )
                    }
                    Text(
                        text = pokemon.species,
                        style = Typography.bodyMedium,
                        color = TextHighlight
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(DefaultSmall)) {
                        pokemon.types.forEach { type -> TypeBadge(type) }
                    }
                }
                Box(
                    modifier = Modifier.weight(0.35f)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(pokemon.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Pokemon Image",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
        item { PokemonTabs(pokemon) }
    }
}


