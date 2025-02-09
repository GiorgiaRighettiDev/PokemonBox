package com.grighetti.pokemonbox.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PokemonSpeciesResponse(
    @SerialName("color") val color: NamedAPIResource,
    @SerialName("evolution_chain") val evolutionChain: EvolutionChainReference,
    @SerialName("genera") val genera: List<Genus>,
    @SerialName("egg_groups") val eggGroups: List<NamedAPIResource>,
    @SerialName("hatch_counter") val hatchCounter: Int,
    @SerialName("gender_rate") val genderRate: Int,
    @SerialName("pokedex_numbers") val pokedexNumbers: List<PokedexEntry>,
    @SerialName("flavor_text_entries") val flavorTextEntries: List<FlavorTextEntry>
)

@Serializable
data class FlavorTextEntry(
    @SerialName("flavor_text") val text: String,
    @SerialName("language") val language: NamedAPIResource,
    @SerialName("version") val version: NamedAPIResource
)

@Serializable
data class PokedexEntry(
    @SerialName("entry_number") val entryNumber: Int,
    @SerialName("pokedex") val pokedex: NamedAPIResource
)

@Serializable
data class Genus(
    @SerialName("genus") val genus: String,
    @SerialName("language") val language: NamedAPIResource
)
