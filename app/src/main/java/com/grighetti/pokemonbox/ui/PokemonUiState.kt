package com.grighetti.pokemonbox.ui

import com.grighetti.pokemonbox.data.domain.PokemonDetail

/**
 * Represents the UI state for the Pokémon list screen.
 *
 * @param pokemonList A list of Pokémon names.
 * @param isLoading Indicates whether new Pokémon are being loaded.
 * @param hasMoreData Indicates whether more data is available for pagination.
 * @param errorMessage An error message in case of failure.
 * @param offset The current pagination offset.
 */
data class PokemonListUiState(
    val pokemonList: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val hasMoreData: Boolean = true,
    val errorMessage: String = "",
    val offset: Int = 0
)

/**
 * Represents the UI state for Pokémon detail retrieval.
 *
 * @param pokemon The Pokémon details, if available.
 * @param isLoading Indicates whether the data is loading.
 * @param errorMessage An error message if an error occurs.
 */
data class PokemonDetailUiState(
    val pokemon: PokemonDetail? = null,
    val isLoading: Boolean = false,
    val errorMessage: String = ""
)
