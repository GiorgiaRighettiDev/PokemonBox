package com.grighetti.pokemonbox.data.remote

import com.grighetti.pokemonbox.data.model.EvolutionChainResponse
import com.grighetti.pokemonbox.data.model.PokemonDetailResponse
import com.grighetti.pokemonbox.data.model.PokemonListResponse
import com.grighetti.pokemonbox.data.model.PokemonSpeciesResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApiService {
    @GET("pokemon/{name}")
    suspend fun getPokemonDetail(@Path("name") name: String): PokemonDetailResponse

    @GET("pokemon-species/{name}")
    suspend fun getPokemonSpecies(@Path("name") name: String): PokemonSpeciesResponse

    @GET("evolution-chain/{id}/")
    suspend fun getEvolutionChain(@Path("id") id: Int): EvolutionChainResponse

    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): PokemonListResponse
}
