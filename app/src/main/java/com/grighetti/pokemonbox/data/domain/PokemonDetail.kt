package com.grighetti.pokemonbox.data.domain

import com.grighetti.pokemonbox.data.model.EvolutionChainResponse
import com.grighetti.pokemonbox.data.model.PokemonDetailResponse
import com.grighetti.pokemonbox.data.model.PokemonSpeciesResponse
import com.grighetti.pokemonbox.utils.Utils
import com.grighetti.pokemonbox.utils.Utils.extractEvolutionChain

/**
 * Domain model representing a Pokémon's details.
 *
 * @property id Unique Pokémon ID.
 * @property name Pokémon's name.
 * @property height Height in meters.
 * @property weight Weight in kilograms.
 * @property types List of Pokémon types (e.g., Fire, Water).
 * @property imageUrl URL for the Pokémon's official artwork.
 * @property abilities List of Pokémon abilities.
 * @property stats Base stats as a map (e.g., "HP" -> 60).
 * @property evolutionChain List of evolution stages for this Pokémon.
 * @property species Species name (e.g., "Seed Pokémon").
 * @property color Primary color of the Pokémon.
 * @property eggGroups List of Pokémon egg groups.
 * @property eggCycle Hatch counter (egg cycle duration).
 * @property genderRatio Male-to-female gender ratio.
 * @property nationalDexNumber The Pokémon's National Pokédex number.
 * @property pokedexEntry The official Pokédex entry for this Pokémon.
 */
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

/**
 * Maps API responses into a [PokemonDetail] domain model.
 */
object PokemonDetailMapper {
    private const val LANGUAGE_EN = "en"
    private const val NATIONAL_DEX = "national"

    fun mapToDomain(
        detailResponse: PokemonDetailResponse,
        speciesResponse: PokemonSpeciesResponse,
        evolutionChainResponse: EvolutionChainResponse
    ): PokemonDetail {
        val nationalDexNumber = speciesResponse.pokedexNumbers
            .firstOrNull { it.pokedex.name == NATIONAL_DEX }?.entryNumber ?: 0

        val evolutionChain = evolutionChainResponse.extractEvolutionChain()

        val officialArtworkUrl = detailResponse.sprites.other?.officialArtwork?.frontDefault
            ?: detailResponse.sprites.frontDefault

        val pokedexEntry = speciesResponse.flavorTextEntries
            .firstOrNull { it.language.name == LANGUAGE_EN }?.text
            ?.replace("\n", " ")
            ?: "No Pokédex entry available"

        val speciesGenus = speciesResponse.genera
            .firstOrNull { it.language.name == LANGUAGE_EN }?.genus ?: "Unknown"

        val genderRatio = Utils.calculateGenderRatio(speciesResponse.genderRate)

        val pokemonId = Utils.extractIdFromUrl(speciesResponse.evolutionChain.url)

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
            species = speciesGenus,
            color = speciesResponse.color.name,
            eggGroups = speciesResponse.eggGroups.map { it.name },
            eggCycle = speciesResponse.hatchCounter.toString(),
            genderRatio = genderRatio,
            nationalDexNumber = nationalDexNumber,
            pokedexEntry = pokedexEntry
        )
    }
}
