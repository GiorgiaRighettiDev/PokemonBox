package com.grighetti.pokemonbox.data.repository

import com.grighetti.pokemonbox.data.remote.PokeApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PokemonRepository @Inject constructor(
    private val api: PokeApiService
) {
    suspend fun getPokemonList(limit: Int, offset: Int) = withContext(Dispatchers.IO) {
        api.getPokemonList(limit, offset)
    }

    suspend fun getPokemonDetail(name: String) = withContext(Dispatchers.IO) {
        api.getPokemonDetail(name)
    }

    suspend fun getPokemonSpecies(name: String) = withContext(Dispatchers.IO) {
        api.getPokemonSpecies(name)
    }

    suspend fun getEvolutionChain(id: Int) = withContext(Dispatchers.IO) {
        api.getEvolutionChain(id)
    }
}
