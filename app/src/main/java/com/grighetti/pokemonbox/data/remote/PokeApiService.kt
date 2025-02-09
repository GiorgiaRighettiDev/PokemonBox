package com.grighetti.pokemonbox.data.remote

import com.grighetti.pokemonbox.data.model.PokemonDetailResponse
import com.grighetti.pokemonbox.data.model.PokemonListResponse
import com.grighetti.pokemonbox.data.model.PokemonSpeciesResponse
import com.grighetti.pokemonbox.data.model.EvolutionChainResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PokeApiService @Inject constructor(
    private val client: HttpClient
) {
    private val BASE_URL = "https://pokeapi.co/api/v2/"

    suspend fun getPokemonList(limit: Int, offset: Int): PokemonListResponse {
        return client.get("$BASE_URL/pokemon") {
            url {
                parameters.append("limit", limit.toString())
                parameters.append("offset", offset.toString())
            }
        }.body()
    }

    suspend fun getPokemonDetail(name: String): PokemonDetailResponse {
        return client.get("$BASE_URL/pokemon/$name").body()
    }

    suspend fun getPokemonSpecies(name: String): PokemonSpeciesResponse {
        return client.get("$BASE_URL/pokemon-species/$name").body()
    }

    suspend fun getEvolutionChain(id: Int): EvolutionChainResponse {
        return client.get("$BASE_URL/evolution-chain/$id").body()
    }
}
