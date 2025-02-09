package com.grighetti.pokemonbox.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PokemonDetailResponse(
    @SerialName("name") val name: String,
    @SerialName("height") val height: Int,
    @SerialName("weight") val weight: Int,
    @SerialName("types") val types: List<TypeWrapper>,
    @SerialName("abilities") val abilities: List<AbilityWrapper>,
    @SerialName("stats") val stats: List<PokemonStat>,
    @SerialName("sprites") val sprites: Sprite
)

@Serializable
data class Sprite(
    @SerialName("front_default") val frontDefault: String?,
    @SerialName("other") val other: OtherSprites?
)

@Serializable
data class OtherSprites(
    @SerialName("official-artwork") val officialArtwork: OfficialArtwork?
)

@Serializable
data class OfficialArtwork(
    @SerialName("front_default") val frontDefault: String?
)

@Serializable
data class PokemonStat(
    @SerialName("base_stat") val baseStat: Int,
    @SerialName("stat") val stat: NamedAPIResource
)

@Serializable
data class AbilityWrapper(
    @SerialName("ability") val ability: NamedAPIResource
)

@Serializable
data class TypeWrapper(
    @SerialName("type") val type: NamedAPIResource
)