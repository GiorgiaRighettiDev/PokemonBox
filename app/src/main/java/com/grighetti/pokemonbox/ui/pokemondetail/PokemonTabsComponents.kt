package com.grighetti.pokemonbox.ui.pokemondetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.grighetti.pokemonbox.R
import com.grighetti.pokemonbox.data.domain.EvolutionStage
import com.grighetti.pokemonbox.data.domain.PokemonDetail
import com.grighetti.pokemonbox.ui.theme.BackgroundColor
import com.grighetti.pokemonbox.ui.theme.DefaultLarge
import com.grighetti.pokemonbox.ui.theme.DefaultMedium
import com.grighetti.pokemonbox.ui.theme.DefaultSmall
import com.grighetti.pokemonbox.ui.theme.StatBadColor
import com.grighetti.pokemonbox.ui.theme.StatGoodColor
import com.grighetti.pokemonbox.ui.theme.TextPrimary
import com.grighetti.pokemonbox.ui.theme.Typography
import com.grighetti.pokemonbox.utils.PokemonInfoUtils


/**
 * Tabs for switching between Pokémon information, stats, and evolution.
 */
@Composable
fun PokemonTabs(pokemon: PokemonDetail) {
    val tabTitles = listOf("Info", "Stats", "Evolution")
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        verticalArrangement = Arrangement.spacedBy(DefaultMedium)
    ) {
        TabRow(selectedTabIndex = selectedTab, containerColor = BackgroundColor) {
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

/**
 * Displays general Pokémon information including height, weight, abilities, and breeding data.
 */
@Composable
fun AboutTab(pokemon: PokemonDetail) {
    Column(
        modifier = Modifier.padding(DefaultMedium),
        verticalArrangement = Arrangement.spacedBy(DefaultSmall)
    ) {
        Text(text = pokemon.pokedexEntry, style = Typography.bodyMedium)
        SectionTitle("About")
        InfoRow("Height", PokemonInfoUtils.formatHeight(pokemon.height))
        InfoRow("Weight", PokemonInfoUtils.formatWeight(pokemon.weight))
        InfoRow("Abilities", pokemon.abilities.joinToString(", "))

        SectionTitle("Breeding")
        InfoRow("Egg Groups", pokemon.eggGroups.joinToString(", "))
        InfoRow("Egg Cycle", pokemon.eggCycle)
    }
}

/**
 * Displays base stats for a Pokémon including a progress bar visualization.
 */
@Composable
fun BaseStatsTab(pokemon: PokemonDetail) {
    val maxStatValue = 150
    val stats = pokemon.stats.mapKeys { PokemonInfoUtils.formatStatName(it.key) }
    val totalStats = stats.values.sum()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(DefaultMedium)
    ) {
        SectionTitle("Base Stats")
        stats.forEach { (name, value) -> StatRow(name, value, maxStatValue) }
        StatRow("Total", totalStats, maxStatValue * 6)
    }
}

/**
 * Displays the Pokémon's evolution chain.
 */
@Composable
fun EvolutionTab(pokemon: PokemonDetail) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(DefaultMedium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SectionTitle("Evolution Chain")
        if (pokemon.evolutionChain.size == 1) {
            NoEvolution()
        } else {
            EvolutionItem(pokemon.evolutionChain.first())
            pokemon.evolutionChain.windowed(2, 1, false).forEach { (_, to) ->
                EvolutionRow(to)
            }
        }
    }
}

/**
 * Displays an evolution stage item with an image and name.
 */
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
        Text(
            text = stage.name.replaceFirstChar { it.uppercase() },
            fontWeight = FontWeight.Bold,
            color = Color(0xFF202749)
        )
    }
}

/**
 * Displays a label-value row in a structured format.
 */
@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = DefaultSmall),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Text(text = value, style = Typography.bodyMedium, color = Color(0xff290402))
    }
}

/**
 * Section title text.
 */
@Composable
fun SectionTitle(title: String) {
    Text(title, style = Typography.titleMedium, modifier = Modifier.padding(vertical = DefaultMedium))
}

/**
 * Displays a single stat row with a label, value, and a visual progress bar.
 *
 * @param name The name of the stat (e.g., "HP", "Attack").
 * @param value The actual value of the stat.
 * @param maxStat The maximum possible value for normalization in the progress bar.
 */
@Composable
fun StatRow(name: String, value: Int, maxStat: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = DefaultSmall),
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
        StatBar(value, maxStat)
    }
}

/**
 * Displays a progress bar representing a stat's value as a percentage of the maxStat.
 *
 * @param value The actual value of the stat.
 * @param maxStat The maximum possible value for normalization.
 */
@Composable
fun StatBar(value: Int, maxStat: Int) {
    val progress = value.toFloat() / maxStat.toFloat()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(DefaultMedium)
            .clip(RoundedCornerShape(DefaultSmall))
            .background(Color.LightGray) // Background of the stat bar
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .height(DefaultMedium)
                .background(if (value >= 50) StatGoodColor else StatBadColor) // Color changes based on value
        )
    }
}

/**
 * Displays a message when a Pokémon does not have an evolution.
 */
@Composable
fun NoEvolution() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(DefaultLarge),
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

/**
 * Displays an evolution transition between two Pokémon in the evolution chain.
 *
 * @param to The target evolution stage.
 */
@Composable
fun EvolutionRow(to: EvolutionStage) {
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
                text = to.evolutionMethod, // Displays the evolution condition (e.g., "Level 16", "Use Fire Stone")
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray,
                style = MaterialTheme.typography.bodyMedium
            )
            EvolutionItem(to)
        }
        Spacer(modifier = Modifier.height(DefaultMedium))
    }
}
