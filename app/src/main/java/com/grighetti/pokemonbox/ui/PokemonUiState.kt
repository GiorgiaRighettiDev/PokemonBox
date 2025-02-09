package com.grighetti.pokemonbox.ui

import com.grighetti.pokemonbox.data.domain.PokemonDetail

data class PokemonUiState(
    val pokemon: PokemonDetail? = null,
    val isLoading: Boolean = false,
    val errorMessage: String = ""
)