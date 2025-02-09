package com.grighetti.pokemonbox.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grighetti.pokemonbox.data.domain.PokemonDetail
import com.grighetti.pokemonbox.data.repository.PokemonRepository
import com.grighetti.pokemonbox.ui.PokemonUiState
import com.grighetti.pokemonbox.utils.Utils
import com.grighetti.pokemonbox.utils.Utils.extractEvolutionChain
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

    // StateFlow to manage the UI state
    private val _uiState = MutableStateFlow(PokemonUiState())
    val uiState: StateFlow<PokemonUiState> = _uiState

    // StateFlow for the list of Pokémon names
    private val _pokemonList = MutableStateFlow<List<String>>(emptyList())
    val pokemonList: StateFlow<List<String>> = _pokemonList

    // Cache to store loaded Pokémon details
    private val _pokemonDetailsCache = MutableStateFlow<Map<String, PokemonDetail>>(emptyMap())
    val pokemonDetailsCache: StateFlow<Map<String, PokemonDetail>> = _pokemonDetailsCache

    var offset = 0
    var isLoading = false
    var hasMoreData = true

    /**
     * Searches for a specific Pokémon by name.
     * If the Pokémon is cached, it updates the UI state immediately.
     * Otherwise, it loads the details and updates the UI once available.
     */
    fun searchPokemon(name: String) {
        viewModelScope.launch {
            Log.d("PokeAPI", "🔎 Searching for: $name")
            _uiState.value = PokemonUiState(isLoading = true)

            // Check if the Pokémon is already cached
            if (_pokemonDetailsCache.value.containsKey(name)) {
                Log.d("PokeAPI", "✅ Pokémon $name found in cache!")
                _uiState.value = PokemonUiState(pokemon = _pokemonDetailsCache.value[name])
                return@launch
            }

            // Load Pokémon details if not cached
            loadPokemonDetail(name)

            // Observe cache updates and update UI when data is available
            _pokemonDetailsCache.collect { cache ->
                cache[name]?.let { pokemon ->
                    _uiState.value = PokemonUiState(pokemon = pokemon)
                    Log.d("PokeAPI", "✅ Pokémon $name successfully loaded!")
                    return@collect
                }
            }
        }
    }

    /**
     * Loads a paginated list of Pokémon names.
     * Avoids unnecessary API calls if data is already loading or there is no more data.
     */
    fun loadPokemonList() {
        if (isLoading || !hasMoreData) return
        isLoading = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("PokeAPI", "📥 Loading Pokémon list: Offset = $offset")

                val response = repository.getPokemonList(limit = 20, offset = offset)
                val names = response.results.map { it.name }

                _pokemonList.update { it + names }
                offset += 20
                hasMoreData = response.next != null

                Log.d("PokeAPI", "✅ Loaded ${names.size} Pokémon")
            } catch (e: Exception) {
                Log.e("PokeAPI", "❌ Error loading Pokémon list", e)
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Loads detailed data for a Pokémon.
     * Uses caching to avoid redundant API requests.
     */
    fun loadPokemonDetail(name: String) {
        if (_pokemonDetailsCache.value.containsKey(name)) return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("PokeAPI", "🔍 Fetching details for: $name")

                val detailResponse = repository.getPokemonDetail(name)
                val speciesResponse = repository.getPokemonSpecies(name)

                val nationalDexNumber = speciesResponse.pokedexNumbers
                    .firstOrNull { it.pokedex.name == "national" }?.entryNumber ?: 0

                val evolutionUrl = speciesResponse.evolutionChain.url
                val pokemonId = Utils.extractIdFromUrl(evolutionUrl)
                val evolutionChainResponse = repository.getEvolutionChain(pokemonId)

                val evolutionChain = evolutionChainResponse.extractEvolutionChain()

                val officialArtworkUrl = detailResponse.sprites.other?.officialArtwork?.frontDefault
                    ?: detailResponse.sprites.frontDefault

                val pokedexEntry = speciesResponse.flavorTextEntries
                    .firstOrNull { it.language.name == "en" }?.text
                    ?.replace("\n", " ")
                    ?: "No Pokédex entry available"

                val newPokemon = PokemonDetail(
                    id = pokemonId,
                    name = detailResponse.name,
                    height = detailResponse.height / 10.0,
                    weight = detailResponse.weight / 10.0,
                    types = detailResponse.types.map { it.type.name },
                    imageUrl = officialArtworkUrl,
                    abilities = detailResponse.abilities.map { it.ability.name },
                    stats = detailResponse.stats.associate { it.stat.name.uppercase() to it.baseStat },
                    evolutionChain = evolutionChain,
                    species = speciesResponse.genera.firstOrNull { it.language.name == "en" }?.genus
                        ?: "Unknown",
                    color = speciesResponse.color.name,
                    eggGroups = speciesResponse.eggGroups.map { it.name },
                    eggCycle = speciesResponse.hatchCounter.toString(),
                    genderRatio = Utils.calculateGenderRatio(speciesResponse.genderRate),
                    nationalDexNumber = nationalDexNumber,
                    pokedexEntry = pokedexEntry
                )

                // Update cache with the new Pokémon details
                _pokemonDetailsCache.update { it + (name to newPokemon) }

            } catch (e: Exception) {
                Log.e("PokeAPI", "❌ Error loading Pokémon detail: $name", e)
            }
        }
    }
}
