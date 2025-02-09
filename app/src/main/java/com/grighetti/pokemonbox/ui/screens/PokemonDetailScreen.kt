package com.grighetti.pokemonbox.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.grighetti.pokemonbox.R
import com.grighetti.pokemonbox.data.domain.EvolutionStage
import com.grighetti.pokemonbox.data.domain.PokemonDetail
import com.grighetti.pokemonbox.ui.TypeBadge
import com.grighetti.pokemonbox.ui.theme.BackgroundColor
import com.grighetti.pokemonbox.ui.theme.DividerColor
import com.grighetti.pokemonbox.ui.theme.ErrorColor
import com.grighetti.pokemonbox.ui.theme.StatBadColor
import com.grighetti.pokemonbox.ui.theme.StatGoodColor
import com.grighetti.pokemonbox.ui.theme.TabIndicatorColor
import com.grighetti.pokemonbox.ui.theme.TextHighlight
import com.grighetti.pokemonbox.ui.theme.TextPrimary
import com.grighetti.pokemonbox.ui.theme.Typography
import com.grighetti.pokemonbox.utils.Utils
import com.grighetti.pokemonbox.viewmodel.PokemonViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailScreen(
    navController: NavController,
    pokemonName: String,
    viewModel: PokemonViewModel = hiltViewModel()
) {
    val uiState by viewModel.pokemonDetailUiState.collectAsState()

    LaunchedEffect(pokemonName) {
        viewModel.searchPokemonDetail(pokemonName) // ✅ Usa la funzione aggiornata
    }

    Scaffold(
        modifier = Modifier.background(BackgroundColor),
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundColor,
                    titleContentColor = TextPrimary,
                    navigationIconContentColor = TextPrimary
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor)
                .padding(16.dp, innerPadding.calculateTopPadding(), 16.dp, 0.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> CircularProgressIndicator()
                uiState.errorMessage.isNotEmpty() -> Text(
                    uiState.errorMessage,
                    color = ErrorColor
                )
                uiState.pokemon != null -> PokemonDetailContent(uiState.pokemon!!)
            }
        }
    }
}

@Composable
fun PokemonDetailContent(pokemon: PokemonDetail) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            PokemonHeader(pokemon)
            Text(
                text = pokemon.pokedexEntry,
                style = Typography.bodyMedium
            )
        }

        item {
            PokemonTabs(pokemon)
        }
    }
}

@Composable
fun PokemonHeader(pokemon: PokemonDetail) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(0.65f)
                    .padding(end = 8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = pokemon.name.replaceFirstChar { it.uppercase() },
                        style = Typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "#${pokemon.nationalDexNumber.toString().padStart(3, '0')}",
                        color = TextHighlight
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = pokemon.species,
                    style = Typography.bodyMedium,
                    color = TextHighlight
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    pokemon.types.forEach { type ->
                        TypeBadge(type)
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }
            Column(
                modifier = Modifier
                    .weight(0.35f)
                    .padding(start = 8.dp),
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                AsyncImage(
                    model = pokemon.imageUrl,
                    contentDescription = "Pokemon Image",
                    modifier = Modifier.size(100.dp)
                )
            }
        }
    }
}

@Composable
fun PokemonTabs(pokemon: PokemonDetail) {
    val tabTitles = listOf("Info", "Stats", "Evolution")
    var selectedTab by remember { mutableIntStateOf(0) }

    Column {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = BackgroundColor,
            divider = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(DividerColor)
                )
            },
            indicator = { tabPositions ->
                Box(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selectedTab])
                        .height(3.dp)
                        .background(TabIndicatorColor)
                )
            }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    modifier = Modifier.background(BackgroundColor),
                    text = {
                        Text(
                            text = title,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                )
            }
        }

        when (selectedTab) {
            0 -> AboutTab(pokemon)
            1 -> BaseStatsTab(pokemon)
            2 -> EvolutionTab(pokemon)
        }
    }
}


@Composable
fun AboutTab(pokemon: PokemonDetail) {
    Column(modifier = Modifier.padding(16.dp)) {
        SectionTitle("About")

        InfoRow(label = "Height", value = Utils.formatHeight(pokemon.height))
        InfoRow(label = "Weight", value = Utils.formatWeight(pokemon.weight))
        InfoRow(label = "Abilities", value = pokemon.abilities.joinToString(", "))

        Spacer(modifier = Modifier.height(16.dp))
        SectionTitle("Breeding")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Gender",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Row {
                Text(
                    text = "♂ ${pokemon.genderRatio.first}%",
                    color = Color(0xFF4285F4),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "♀ ${pokemon.genderRatio.second}%",
                    color = Color(0xFFD81B60),
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        InfoRow(label = "Egg Groups", value = pokemon.eggGroups.joinToString(", "))
        InfoRow(label = "Egg Cycle", value = pokemon.eggCycle)
    }
}

@Composable
fun BaseStatsTab(pokemon: PokemonDetail) {
    val maxStatValue = 150
    val stats = pokemon.stats.mapKeys { Utils.formatStatName(it.key) }
    val totalStats = stats.values.sum()
    val totalProgress = totalStats / (maxStatValue * 6).toFloat()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        SectionTitle("Base Stats")

        stats.forEach { (name, value) ->
            val progress = value / maxStatValue.toFloat()
            StatRow(name, value, progress)
        }

        Spacer(modifier = Modifier.height(16.dp))

        StatRow("Total", totalStats, totalProgress)
    }
}


@Composable
fun StatRow(name: String, value: Int, progress: Float) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            modifier = Modifier.width(80.dp),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = value.toString(),
            modifier = Modifier.width(40.dp),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.LightGray)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(8.dp)
                    .background(if (value >= 50) StatGoodColor else StatBadColor)
            )
        }
    }
}

@Composable
fun EvolutionTab(pokemon: PokemonDetail) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SectionTitle("Evolution Chain")
        Spacer(modifier = Modifier.height(16.dp))

        if (pokemon.evolutionChain.size == 1) {
            NoEvolution()
        } else {
            // Show the first Pokémon centered at the top
            EvolutionItem(pokemon.evolutionChain.first())
            Spacer(modifier = Modifier.height(24.dp))

            // Show the rest of the evolution chain
            pokemon.evolutionChain.windowed(2, 1, false).forEach { (from, to) ->
                EvolutionRow(from, to)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun NoEvolution() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.pkm_det_no_evolution),
            fontWeight = FontWeight.SemiBold,
            color = Color.Gray,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun EvolutionRow(from: EvolutionStage, to: EvolutionStage) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = to.evolutionMethod,
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray,
                style = MaterialTheme.typography.bodyMedium
            )
            EvolutionItem(to)
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun EvolutionItem(stage: EvolutionStage) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(stage.imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = stage.name,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stage.name.replaceFirstChar { it.uppercase() },
            fontWeight = FontWeight.Bold,
            color = Color(0xFF202749)
        )
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Text(
            text = value.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xff290402)
        )
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        title,
        style = Typography.titleMedium
    )
    Spacer(modifier = Modifier.height(8.dp))
}
