package com.grighetti.pokemonbox.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EvolutionChainReference(
    @SerialName("url") val url: String
)

@Serializable
data class EvolutionChainResponse(
    @SerialName("chain") val chain: EvolutionChainLink
)

@Serializable
data class EvolutionChainLink(
    @SerialName("species") val species: NamedAPIResource,
    @SerialName("evolves_to") val evolvesTo: List<EvolutionChainLink>,
    @SerialName("evolution_details") val evolutionDetails: List<EvolutionDetail> = emptyList()
)

@Serializable
data class EvolutionDetail(
    @SerialName("min_level") val minLevel: Int? = null,
    @SerialName("trigger") val trigger: NamedAPIResource? = null,
    @SerialName("item") val item: NamedAPIResource? = null,
    @SerialName("gender") val gender: Int? = null,
    @SerialName("held_item") val heldItem: NamedAPIResource? = null,
    @SerialName("known_move") val knownMove: NamedAPIResource? = null,
    @SerialName("known_move_type") val knownMoveType: NamedAPIResource? = null,
    @SerialName("location") val location: NamedAPIResource? = null,
    @SerialName("min_affection") val minAffection: Int? = null,
    @SerialName("min_beauty") val minBeauty: Int? = null,
    @SerialName("min_happiness") val minHappiness: Int? = null,
    @SerialName("needs_overworld_rain") val needsOverworldRain: Boolean = false,
    @SerialName("party_species") val partySpecies: NamedAPIResource? = null,
    @SerialName("party_type") val partyType: NamedAPIResource? = null,
    @SerialName("relative_physical_stats") val relativePhysicalStats: Int? = null,
    @SerialName("time_of_day") val timeOfDay: String = "",
    @SerialName("trade_species") val tradeSpecies: NamedAPIResource? = null,
    @SerialName("turn_upside_down") val turnUpsideDown: Boolean = false
)


