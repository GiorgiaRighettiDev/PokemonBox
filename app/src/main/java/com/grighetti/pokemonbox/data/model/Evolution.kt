package com.grighetti.pokemonbox.data.model

import android.util.Log
import com.grighetti.pokemonbox.data.domain.EvolutionStage
import com.grighetti.pokemonbox.utils.PokemonInfoUtils
import com.grighetti.pokemonbox.utils.PokemonInfoUtils.extractIdFromUrl
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Locale

/**
 * Reference to an evolution chain.
 * This object contains only a URL that points to the full evolution chain.
 *
 * @property url The API endpoint URL for the evolution chain.
 */
@Serializable
data class EvolutionChainReference(
    @SerialName("url") val url: String
)

/**
 * Represents the response for a Pokémon's evolution chain.
 *
 * @property chain The root of the evolution chain.
 */
@Serializable
data class EvolutionChainResponse(
    @SerialName("chain") val chain: EvolutionChainLink
)

/**
 * Extracts the evolution chain from an API response and maps it to [EvolutionStage] objects.
 *
 * @return A list of [EvolutionStage] representing the Pokémon's evolution stages.
 */
fun EvolutionChainResponse.extractEvolutionChain(): List<EvolutionStage> {
    val evolutionStages = mutableListOf<EvolutionStage>()

    fun extractStages(chain: EvolutionChainLink) {
        val id = extractIdFromUrl(chain.species.url)

        val evolutionDetail = chain.evolutionDetails.firstOrNull()
        val evolutionLevel = evolutionDetail?.minLevel ?: -1

        val evolutionTrigger = evolutionDetail?.trigger?.name ?: "Unknown"
        val evolutionItem =
            evolutionDetail?.item?.name?.replace("-", " ")?.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.ROOT
                ) else it.toString()
            }

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
                imageUrl = PokemonInfoUtils.getPokemonImageUrl(id),
                evolutionMethod = evolutionMethodString
            )
        )

        chain.evolvesTo.forEach { nextStage ->
            extractStages(nextStage)
        }
    }

    extractStages(this.chain)
    return evolutionStages
}


/**
 * Represents a single node in an evolution chain.
 * Each Pokémon species can evolve into multiple Pokémon.
 *
 * @property species The Pokémon species at this stage of evolution.
 * @property evolvesTo The list of Pokémon species this species evolves into.
 * @property evolutionDetails The conditions required for evolution.
 */
@Serializable
data class EvolutionChainLink(
    @SerialName("species") val species: NamedAPIResource,
    @SerialName("evolves_to") val evolvesTo: List<EvolutionChainLink> = emptyList(),
    @SerialName("evolution_details") val evolutionDetails: List<EvolutionDetail> = emptyList()
)

/**
 * Represents the detailed conditions required for a Pokémon to evolve.
 *
 * @property minLevel The minimum level required for evolution, if applicable.
 * @property trigger The trigger that causes evolution (e.g., leveling up, using an item).
 * @property item The item required for evolution, if applicable.
 * @property gender The required gender for evolution (0 = male, 1 = female, null = any).
 * @property heldItem The item the Pokémon must hold to evolve, if applicable.
 * @property knownMove The move the Pokémon must know to evolve, if applicable.
 * @property knownMoveType The type of move the Pokémon must know to evolve, if applicable.
 * @property location The location where evolution must occur, if applicable.
 * @property minAffection The minimum affection required for evolution, if applicable.
 * @property minBeauty The minimum beauty required for evolution, if applicable.
 * @property minHappiness The minimum happiness required for evolution, if applicable.
 * @property needsOverworldRain Whether it must be raining in the overworld for evolution.
 * @property partySpecies A required Pokémon species in the party for evolution, if applicable.
 * @property partyType A required Pokémon type in the party for evolution, if applicable.
 * @property relativePhysicalStats A stat comparison condition for evolution, if applicable.
 * @property timeOfDay The required time of day for evolution (e.g., "day", "night").
 * @property tradeSpecies The Pokémon species that must be traded for evolution, if applicable.
 * @property turnUpsideDown Whether the console must be held upside-down for evolution.
 */
@Serializable
data class EvolutionDetail(
    @SerialName("min_level") val minLevel: Int? = null,
    @SerialName("trigger") val trigger: NamedAPIResource? = null,
    @SerialName("item") val item: NamedAPIResource? = null,
    @SerialName("gender") val gender: Int? = null,
    @SerialName("held_item") val heldItem: NamedAPIResource? = null,
    @SerialName("known_move") val knownMove: NamedAPIResource? = null,
    @SerialName("known_move_type") val knownMoveType: NamedAPIResource? = null,
    @SerialName("location") val location: NamedAPIResource? = null,
    @SerialName("min_affection") val minAffection: Int? = null,
    @SerialName("min_beauty") val minBeauty: Int? = null,
    @SerialName("min_happiness") val minHappiness: Int? = null,
    @SerialName("needs_overworld_rain") val needsOverworldRain: Boolean = false,
    @SerialName("party_species") val partySpecies: NamedAPIResource? = null,
    @SerialName("party_type") val partyType: NamedAPIResource? = null,
    @SerialName("relative_physical_stats") val relativePhysicalStats: Int? = null,
    @SerialName("time_of_day") val timeOfDay: String = "",
    @SerialName("trade_species") val tradeSpecies: NamedAPIResource? = null,
    @SerialName("turn_upside_down") val turnUpsideDown: Boolean = false
)
