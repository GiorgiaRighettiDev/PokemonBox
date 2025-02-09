package com.grighetti.pokemonbox.data.remote

import com.grighetti.pokemonbox.data.model.EvolutionChainResponse
import com.grighetti.pokemonbox.data.model.PokemonDetailResponse
import com.grighetti.pokemonbox.data.model.PokemonListResponse
import com.grighetti.pokemonbox.data.model.PokemonSpeciesResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service class responsible for making network requests to the Pokémon API using Ktor.
 */
@Singleton
class PokeApiService @Inject constructor(
    private val client: HttpClient
) {
    private val BASE_URL = "https://pokeapi.co/api/v2/"

    /**
     * Fetches a paginated list of Pokémon.
     *
     * @param limit The number of Pokémon to retrieve per request.
     * @param offset The starting position in the Pokémon list.
     * @return [PokemonListResponse] containing Pokémon names and URLs.
     * @throws Exception if the request fails.
     */
    suspend fun getPokemonList(limit: Int, offset: Int): PokemonListResponse {
        return safeApiCall {
            client.get("$BASE_URL/pokemon") {
                url {
                    parameters.append("limit", limit.toString())
                    parameters.append("offset", offset.toString())
                }
            }.body()
        }
    }

    /**
     * Fetches detailed data for a specific Pokémon.
     *
     * @param name The Pokémon's name or ID.
     * @return [PokemonDetailResponse] containing detailed stats.
     * @throws Exception if the request fails.
     */
    suspend fun getPokemonDetail(name: String): PokemonDetailResponse {
        return safeApiCall {
            client.get("$BASE_URL/pokemon/$name").body()
        }
    }

    /**
     * Fetches species-specific data for a Pokémon.
     *
     * @param name The Pokémon's name or ID.
     * @return [PokemonSpeciesResponse] containing species-related information.
     * @throws Exception if the request fails.
     */
    suspend fun getPokemonSpecies(name: String): PokemonSpeciesResponse {
        return safeApiCall {
            client.get("$BASE_URL/pokemon-species/$name").body()
        }
    }

    /**
     * Fetches the evolution chain for a Pokémon.
     *
     * @param id The ID of the evolution chain.
     * @return [EvolutionChainResponse] containing evolution details.
     * @throws Exception if the request fails.
     */
    suspend fun getEvolutionChain(id: Int): EvolutionChainResponse {
        return safeApiCall {
            client.get("$BASE_URL/evolution-chain/$id").body()
        }
    }

    /**
     * A helper function to handle API requests safely.
     * Catches exceptions and logs errors while keeping the app stable.
     *
     * @param request The suspend function making the API call.
     * @return The expected response type.
     * @throws Exception if the request fails.
     */
    private suspend fun <T> safeApiCall(request: suspend () -> T): T {
        return withContext(Dispatchers.IO) {
            try {
                request()
            } catch (e: Exception) {
                throw ApiException("API request failed: ${e.message}", e)
            }
        }
    }
}

/**
 * Custom exception class for API-related errors.
 */
class ApiException(message: String, cause: Throwable? = null) : Exception(message, cause)
