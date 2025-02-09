package com.grighetti.pokemonbox.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grighetti.pokemonbox.data.domain.PokemonDetail
import com.grighetti.pokemonbox.data.domain.PokemonDetailMapper
import com.grighetti.pokemonbox.data.repository.PokemonRepository
import com.grighetti.pokemonbox.ui.PokemonDetailUiState
import com.grighetti.pokemonbox.ui.PokemonListUiState
import com.grighetti.pokemonbox.utils.PokemonInfoUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PokemonViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    private val _pokemonListUiState = MutableStateFlow(PokemonListUiState())
    val pokemonListUiState: StateFlow<PokemonListUiState> = _pokemonListUiState

    private val _pokemonDetailUiState = MutableStateFlow(PokemonDetailUiState())
    val pokemonDetailUiState: StateFlow<PokemonDetailUiState> = _pokemonDetailUiState

    private val _pokemonDetailsCache = MutableStateFlow<Map<String, PokemonDetail>>(emptyMap())
    val pokemonDetailsCache: StateFlow<Map<String, PokemonDetail>> = _pokemonDetailsCache

    /**
     * Loads the Pokémon list in a paginated way, updating the UI as each Pokémon is retrieved.
     */
    fun loadPokemonList() {
        val currentState = _pokemonListUiState.value
        if (currentState.isLoading || !currentState.hasMoreData) return

        _pokemonListUiState.update { it.copy(isLoading = true, errorMessage = "") }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("PokeAPI", "📥 Fetching Pokémon list: Offset = ${currentState.offset}")

                val response = repository.getPokemonList(limit = 20, offset = currentState.offset)
                val names = response.results.map { it.name }

                names.forEach { pokemonName ->
                    _pokemonListUiState.update {
                        it.copy(pokemonList = it.pokemonList + pokemonName)
                    }
                }

                _pokemonListUiState.update {
                    it.copy(
                        hasMoreData = response.next != null,
                        isLoading = false,
                        offset = it.offset + 20
                    )
                }

                Log.d("PokeAPI", "✅ Loaded ${names.size} Pokémon")
            } catch (e: Exception) {
                Log.e("PokeAPI", "❌ Error loading Pokémon list", e)
                _pokemonListUiState.update {
                    it.copy(isLoading = false, errorMessage = "Error loading Pokémon list")
                }
            }
        }
    }

    /**
     * Fetches detailed Pokémon data. If cached, updates the UI immediately, otherwise requests from the API.
     */
    fun searchPokemonDetail(name: String) {
        val cachedPokemon = _pokemonDetailsCache.value[name]

        if (cachedPokemon != null) {
            _pokemonDetailUiState.update { it.copy(isLoading = false, pokemon = cachedPokemon) }
            return
        }

        _pokemonDetailUiState.update { it.copy(isLoading = true, errorMessage = "") }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("PokeAPI", "🔍 Fetching details for: $name")

                val detailResponse = repository.getPokemonDetail(name)
                val speciesResponse = repository.getPokemonSpecies(name)
                val evolutionChainResponse = repository.getEvolutionChain(
                    PokemonInfoUtils.extractIdFromUrl(speciesResponse.evolutionChain.url)
                )

                val newPokemon = PokemonDetailMapper.mapToDomain(
                    detailResponse, speciesResponse, evolutionChainResponse
                )

                _pokemonDetailsCache.update { it + (name to newPokemon) }
                _pokemonDetailUiState.update { it.copy(isLoading = false, pokemon = newPokemon) }

            } catch (e: Exception) {
                Log.e("PokeAPI", "❌ Error loading Pokémon detail: $name", e)
                _pokemonDetailUiState.update {
                    it.copy(isLoading = false, errorMessage = "Failed to load Pokémon.")
                }
            }
        }
    }
}
