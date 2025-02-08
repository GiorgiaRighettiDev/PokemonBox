package com.grighetti.pokemonbox.data.model

data class PokemonListResponse(
    val results: List<PokemonListItem>,
    val next: String?
)

data class PokemonListItem(
    val name: String,
    val url: String
)
