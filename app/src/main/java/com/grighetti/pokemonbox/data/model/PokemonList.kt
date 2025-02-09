package com.grighetti.pokemonbox.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PokemonListResponse(
    @SerialName("results") val results: List<PokemonListItem>,
    @SerialName("next") val next: String?
)

@Serializable
data class PokemonListItem(
    @SerialName("name") val name: String,
    @SerialName("url") val url: String
)
