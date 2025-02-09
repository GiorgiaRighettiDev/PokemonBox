package com.grighetti.pokemonbox.utils

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.grighetti.pokemonbox.data.domain.EvolutionStage
import com.grighetti.pokemonbox.data.model.EvolutionChainLink
import com.grighetti.pokemonbox.data.model.EvolutionChainResponse
import com.grighetti.pokemonbox.ui.theme.DefaultMedium
import java.util.Locale

object Utils {

    /**
     * Formats height from meters to feet and inches.
     * Example: 1.7m -> "5'7" (1.7m)"
     */
    fun formatHeight(heightInMeters: Double): String {
        val inches = (heightInMeters * 39.37).toInt()
        val feet = inches / 12
        val remainingInches = inches % 12
        return "${feet}'${remainingInches}\" (${heightInMeters}m)"
    }

    /**
     * Formats weight from kilograms to pounds.
     * Example: 60kg -> "132.3 lbs (60.0 kg)"
     */
    fun formatWeight(weightInKg: Double): String {
        val pounds = weightInKg * 2.20462
        return "%.1f lbs (%.1f kg)".format(pounds, weightInKg)
    }

    /**
     * Formats Pokémon stat names to a readable format.
     * Example: "special-attack" -> "Sp. Atk"
     */
    fun formatStatName(stat: String): String {
        return when (stat.lowercase()) {
            "special-attack" -> "Sp. Atk"
            "special-defense" -> "Sp. Def"
            "hp" -> "HP"
            else -> stat.split("-")
                .joinToString(" ") { it.lowercase().replaceFirstChar { ch -> ch.uppercase() } }
        }
    }

    val colorMap = mapOf(
        "black" to Color(0xFF5C5C5C),
        "blue" to Color(0xFFA3D4FB),
        "brown" to Color(0xFFD2BEB8),
        "gray" to Color(0xFFE1E8EC),
        "green" to Color(0xFFB8E1B9),
        "pink" to Color(0xFFFAC4DB),
        "purple" to Color(0xFFDCA8E5),
        "red" to Color(0xFFF3B1B1),
        "white" to Color(0xFFFFFFFF),
        "yellow" to Color(0xFFFFF8B2),
    )

    fun parseColor(colorName: String): Color {
        return colorMap[colorName.lowercase()] ?: Color(0xFFEAEAEA)
    }


    fun calculateGenderRatio(genderRate: Int): Pair<Double, Double> {
        return when (genderRate) {
            -1 -> Pair(0.0, 0.0)  // Pokémon senza genere (tipo Ditto)
            0 -> Pair(100.0, 0.0) // Solo maschio
            1 -> Pair(87.5, 12.5)
            2 -> Pair(75.0, 25.0)
            3 -> Pair(62.5, 37.5)
            4 -> Pair(50.0, 50.0) // Equilibrato
            5 -> Pair(37.5, 62.5)
            6 -> Pair(25.0, 75.0)
            7 -> Pair(12.5, 87.5)
            8 -> Pair(0.0, 100.0) // Solo femmina
            else -> Pair(0.0, 0.0)
        }
    }

    fun extractIdFromUrl(url: String): Int {
        return url.split("/").dropLast(1).last().toInt()
    }

    private fun getPokemonImageUrl(pokemonId: Int): String {
        return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$pokemonId.png"
    }


    fun EvolutionChainResponse.extractEvolutionChain(): List<EvolutionStage> {
        val evolutionStages = mutableListOf<EvolutionStage>()

        fun extractStages(chain: EvolutionChainLink, parentName: String? = null) {
            val id = Utils.extractIdFromUrl(chain.species.url)

            val evolutionDetail = chain.evolutionDetails.firstOrNull()
            val evolutionLevel = evolutionDetail?.minLevel ?: -1

            val evolutionTrigger = evolutionDetail?.trigger?.name ?: "Unknown"
            val evolutionItem =
                evolutionDetail?.item?.name?.replace("-", " ")?.capitalize(Locale.ROOT)

            Log.d(
                "EvolutionChain",
                "🔄 Processing: ${chain.species.name} ➡ MinLevel: $evolutionLevel | Trigger: $evolutionTrigger"
            )

            val evolutionMethodString = when (evolutionTrigger) {
                "level-up" -> if (evolutionLevel > 0) "Level $evolutionLevel" else "Level Up"
                "use-item" -> "Use $evolutionItem"
                "trade" -> "Trade"
                else -> "Unknown Evolution Method"
            }

            evolutionStages.add(
                EvolutionStage(
                    name = chain.species.name.replaceFirstChar { it.uppercase() },
                    level = evolutionLevel,
                    imageUrl = Utils.getPokemonImageUrl(id),
                    evolutionMethod = evolutionMethodString
                )
            )

            chain.evolvesTo.forEach { nextStage ->
                extractStages(nextStage, chain.species.name)
            }
        }

        extractStages(this.chain)
        return evolutionStages
    }


}
