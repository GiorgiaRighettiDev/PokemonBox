package com.grighetti.pokemonbox.data.domain

import com.grighetti.pokemonbox.data.model.EvolutionChainResponse
import com.grighetti.pokemonbox.data.model.PokemonDetailResponse
import com.grighetti.pokemonbox.data.model.PokemonSpeciesResponse
import com.grighetti.pokemonbox.utils.Utils
import com.grighetti.pokemonbox.utils.Utils.extractEvolutionChain


data class PokemonDetail(
    val id: Int,
    val name: String,
    val height: Double,
    val weight: Double,
    val types: List<String>,
    val imageUrl: String?,
    val abilities: List<String>,
    val stats: Map<String, Int>,
    val evolutionChain: List<EvolutionStage>,
    val species: String,
    val color: String,
    val eggGroups: List<String>,
    val eggCycle: String,
    val genderRatio: Pair<Double, Double>,
    val nationalDexNumber: Int,
    val pokedexEntry: String
)

object PokemonDetailMapper {
    fun mapToDomain(
        detailResponse: PokemonDetailResponse,
        speciesResponse: PokemonSpeciesResponse,
        evolutionChainResponse: EvolutionChainResponse
    ): PokemonDetail {
        val nationalDexNumber = speciesResponse.pokedexNumbers
            .firstOrNull { it.pokedex.name == "national" }?.entryNumber ?: 0

        val evolutionChain = evolutionChainResponse.extractEvolutionChain()

        val officialArtworkUrl = detailResponse.sprites.other?.officialArtwork?.frontDefault
            ?: detailResponse.sprites.frontDefault

        val pokedexEntry = speciesResponse.flavorTextEntries
            .firstOrNull { it.language.name == "en" }?.text
            ?.replace("\n", " ")
            ?: "No Pokédex entry available"

        val evolutionUrl = speciesResponse.evolutionChain.url

        val pokemonId = Utils.extractIdFromUrl(evolutionUrl)

        return PokemonDetail(
            id = pokemonId,
            name = detailResponse.name.replaceFirstChar { it.uppercase() },
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
    }
}
