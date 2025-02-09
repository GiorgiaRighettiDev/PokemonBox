package com.grighetti.pokemonbox.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents species-related information of a Pokémon.
 *
 * @property color The primary color of the Pokémon species.
 * @property evolutionChain Reference to the evolution chain of this Pokémon.
 * @property genera List of localized genus descriptions.
 * @property eggGroups List of egg groups the Pokémon belongs to.
 * @property hatchCounter The number of cycles required to hatch an egg.
 * @property genderRate The gender distribution (-1 means genderless).
 * @property pokedexNumbers List of Pokédex numbers associated with this species.
 * @property flavorTextEntries List of Pokédex flavor text descriptions.
 */
@Serializable
data class PokemonSpeciesResponse(
    @SerialName("color") val color: NamedAPIResource,
    @SerialName("evolution_chain") val evolutionChain: EvolutionChainReference,
    @SerialName("genera") val genera: List<Genus> = emptyList(),
    @SerialName("egg_groups") val eggGroups: List<NamedAPIResource> = emptyList(),
    @SerialName("hatch_counter") val hatchCounter: Int,
    @SerialName("gender_rate") val genderRate: Int,
    @SerialName("pokedex_numbers") val pokedexNumbers: List<PokedexEntry> = emptyList(),
    @SerialName("flavor_text_entries") val flavorTextEntries: List<FlavorTextEntry> = emptyList()
)

/**
 * Represents a Pokédex entry for a Pokémon species.
 *
 * @property entryNumber The index number in the specified Pokédex.
 * @property pokedex The reference to the specific Pokédex.
 */
@Serializable
data class PokedexEntry(
    @SerialName("entry_number") val entryNumber: Int,
    @SerialName("pokedex") val pokedex: NamedAPIResource
)

/**
 * Represents localized flavor text descriptions from the Pokédex.
 *
 * @property text The descriptive text for this Pokémon species.
 * @property language The language of the flavor text.
 * @property version The game version associated with the flavor text.
 */
@Serializable
data class FlavorTextEntry(
    @SerialName("flavor_text") val text: String,
    @SerialName("language") val language: NamedAPIResource,
    @SerialName("version") val version: NamedAPIResource
)

/**
 * Represents the genus classification of a Pokémon species.
 *
 * @property genus The localized genus name.
 * @property language The language of the genus name.
 */
@Serializable
data class Genus(
    @SerialName("genus") val genus: String,
    @SerialName("language") val language: NamedAPIResource
)
