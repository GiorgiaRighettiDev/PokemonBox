package com.grighetti.pokemonbox.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the paginated response for a list of Pokémon.
 *
 * @property results List of Pokémon entries containing names and URLs.
 * @property next The URL for the next page of Pokémon (null if no more pages).
 */
@Serializable
data class PokemonListResponse(
    @SerialName("results") val results: List<PokemonListItem>,
    @SerialName("next") val next: String? = null
)

/**
 * Represents a single Pokémon entry in the paginated list.
 *
 * @property name The name of the Pokémon.
 * @property url The API endpoint to fetch detailed Pokémon data.
 */
@Serializable
data class PokemonListItem(
    @SerialName("name") val name: String,
    @SerialName("url") val url: String
)
