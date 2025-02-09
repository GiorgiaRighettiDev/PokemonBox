package com.grighetti.pokemonbox.data.repository

import com.grighetti.pokemonbox.data.model.*
import com.grighetti.pokemonbox.data.remote.PokeApiService
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository class responsible for fetching Pokémon data from the API.
 * Acts as an abstraction layer between the ViewModel and the API service.
 *
 * @property api The API service that interacts with the Pokémon API.
 */
@Singleton
class PokemonRepository @Inject constructor(
    private val api: PokeApiService
) {

    /**
     * Retrieves a paginated list of Pokémon.
     *
     * @param limit The number of Pokémon to retrieve per request.
     * @param offset The starting index for the Pokémon list.
     * @return [PokemonListResponse] containing the list of Pokémon.
     */
    suspend fun getPokemonList(limit: Int, offset: Int): PokemonListResponse =
        api.getPokemonList(limit, offset)

    /**
     * Retrieves details for a specific Pokémon.
     *
     * @param name The name of the Pokémon.
     * @return [PokemonDetailResponse] containing detailed information.
     */
    suspend fun getPokemonDetail(name: String): PokemonDetailResponse =
        api.getPokemonDetail(name)

    /**
     * Retrieves species-related information for a specific Pokémon.
     *
     * @param name The name of the Pokémon.
     * @return [PokemonSpeciesResponse] containing species-related details.
     */
    suspend fun getPokemonSpecies(name: String): PokemonSpeciesResponse =
        api.getPokemonSpecies(name)

    /**
     * Retrieves the evolution chain for a Pokémon.
     *
     * @param id The ID of the evolution chain.
     * @return [EvolutionChainResponse] containing the evolution details.
     */
    suspend fun getEvolutionChain(id: Int): EvolutionChainResponse =
        api.getEvolutionChain(id)
}
