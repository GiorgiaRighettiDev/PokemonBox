package com.grighetti.pokemonbox.ui

import com.grighetti.pokemonbox.data.model.PokemonDetail

// Modello UI State
data class PokemonUiState(
    val pokemon: PokemonDetail? = null,
    val isLoading: Boolean = false,
    val errorMessage: String = ""
)