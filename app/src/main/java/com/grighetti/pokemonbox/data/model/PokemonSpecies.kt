package com.grighetti.pokemonbox.data.model

import com.google.gson.annotations.SerializedName

data class PokemonSpeciesResponse(
    @SerializedName("color") val color: NamedAPIResource,
    @SerializedName("evolution_chain") val evolutionChain: EvolutionChainReference,
    @SerializedName("genera") val genera: List<Genus>,
    @SerializedName("egg_groups") val eggGroups: List<NamedAPIResource>,
    @SerializedName("hatch_counter") val hatchCounter: Int,
    @SerializedName("gender_rate") val genderRate: Int,
    @SerializedName("pokedex_numbers") val pokedexNumbers: List<PokedexEntry>,
    @SerializedName("flavor_text_entries") val flavorTextEntries: List<FlavorTextEntry> // ✅ Aggiunto
)


data class FlavorTextEntry(
    @SerializedName("flavor_text") val text: String,
    val language: NamedAPIResource,
    val version: NamedAPIResource
)

data class PokedexEntry(
    @SerializedName("entry_number") val entryNumber: Int,
    @SerializedName("pokedex") val pokedex: NamedAPIResource
)

data class Genus(
    @SerializedName("genus") val genus: String,
    val language: NamedAPIResource
)
