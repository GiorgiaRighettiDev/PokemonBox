package com.grighetti.pokemonbox.data.repository

import com.grighetti.pokemonbox.data.remote.PokeApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PokemonRepository @Inject constructor(
    private val api: PokeApiService
) {
    suspend fun getPokemonList(limit: Int, offset: Int) = api.getPokemonList(limit, offset)
    suspend fun getPokemonDetail(name: String) = api.getPokemonDetail(name)
    suspend fun getPokemonSpecies(name: String) = api.getPokemonSpecies(name)
    suspend fun getEvolutionChain(id: Int) = api.getEvolutionChain(id)
}
