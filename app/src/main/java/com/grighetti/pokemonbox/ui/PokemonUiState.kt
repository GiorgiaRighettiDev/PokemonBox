package com.grighetti.pokemonbox.ui

import com.grighetti.pokemonbox.data.domain.PokemonDetail

data class PokemonDetailState(
    val isLoading: Boolean = false,
    val pokemon: PokemonDetail? = null,
    val errorMessage: String = ""
)

data class PokemonUiState(
    val pokemon: PokemonDetail? = null,
    val isLoading: Boolean = false,
    val errorMessage: String = ""
)

data class PokemonListUiState(
    val pokemonList: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val hasMoreData: Boolean = true,
    val errorMessage: String = "",
    val offset: Int = 0 // ✅ Ora l'offset è gestito qui
)


/**
 * Stato dei dettagli di un singolo Pokémon.
 */
data class PokemonDetailUiState(
    val pokemon: PokemonDetail? = null,
    val isLoading: Boolean = false,
    val errorMessage: String = ""
)
