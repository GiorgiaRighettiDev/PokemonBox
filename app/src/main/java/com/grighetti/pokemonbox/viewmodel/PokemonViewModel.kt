package com.grighetti.pokemonbox.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grighetti.pokemonbox.data.model.PokemonDetail
import com.grighetti.pokemonbox.data.model.extractEvolutionChain
import com.grighetti.pokemonbox.data.repository.PokemonRepository
import com.grighetti.pokemonbox.ui.PokemonUiState
import com.grighetti.pokemonbox.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PokemonViewModel @Inject constructor(
    private val repository: PokemonRepository // ✅ Iniettato con Hilt
) : ViewModel() {

    private val _uiState = MutableStateFlow(PokemonUiState())
    val uiState: StateFlow<PokemonUiState> = _uiState

    private val _pokemonList = MutableStateFlow<List<String>>(emptyList())
    val pokemonList: StateFlow<List<String>> = _pokemonList

    var offset = 0
    var isLoading = false
    var hasMoreData = true

    fun searchPokemon(name: String) {
        viewModelScope.launch {
            Log.d("PokeAPI", "🔎 Searching for: $name")
            _uiState.value = PokemonUiState(isLoading = true)

            try {
                val pokemon = getPokemonDetail(name.lowercase())
                _uiState.value = PokemonUiState(pokemon = pokemon)
                Log.d("PokeAPI", "✅ Details loaded for: ${pokemon.name}")
            } catch (e: Exception) {
                Log.e("PokeAPI", "❌ Error fetching Pokémon: $name", e)
                _uiState.value = PokemonUiState(errorMessage = "Pokémon not found")
            }
        }
    }

    fun loadPokemonList() {
        if (isLoading || !hasMoreData) return  // Prevent multiple unnecessary requests
        isLoading = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("PokeAPI", "📥 Loading Pokémon list: Offset = $offset")

                val response = repository.getPokemonList(limit = 20, offset = offset)
                val names = response.results.map { it.name }

                _pokemonList.value += names
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

    suspend fun getPokemonDetail(name: String): PokemonDetail =
        withContext(Dispatchers.IO) {
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
                ?: detailResponse.sprites.frontDefault // ✅ Fallback to default sprite if artwork is missing

            val pokedexEntry = speciesResponse.flavorTextEntries
                .firstOrNull { it.language.name == "en" }?.text
                ?.replace("\n", " ") // ✅ Rimuove gli a capo strani
                ?: "No Pokédex entry available"

            return@withContext PokemonDetail(
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
                pokedexEntry = pokedexEntry // ✅ Aggiunto
            )

        }

    /*
        suspend fun oldGetPokemonDetail(name: String): PokemonDetail =
            withContext(Dispatchers.IO) {
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
                    ?: detailResponse.sprites.frontDefault // ✅ Fallback to default sprite if artwork is missing
    
    
                return@withContext PokemonDetail(
                    id = pokemonId,
                    name = detailResponse.name,
                    height = detailResponse.height / 10.0,
                    weight = detailResponse.weight / 10.0,
                    types = detailResponse.types.map { it.type.name },
                    imageUrl = officialArtworkUrl, // ✅ Using official artwork
                    abilities = detailResponse.abilities.map { it.ability.name },
                    stats = detailResponse.stats.associate { it.stat.name.uppercase() to it.baseStat },
                    evolutionChain = evolutionChain,
                    species = speciesResponse.genera.firstOrNull { it.language.name == "en" }?.genus
                        ?: "Unknown",
                    color = speciesResponse.color.name,
                    eggGroups = speciesResponse.eggGroups.map { it.name },
                    eggCycle = speciesResponse.hatchCounter.toString(),
                    genderRatio = Utils.calculateGenderRatio(speciesResponse.genderRate),
                    nationalDexNumber = nationalDexNumber
                )
            }*/

}
