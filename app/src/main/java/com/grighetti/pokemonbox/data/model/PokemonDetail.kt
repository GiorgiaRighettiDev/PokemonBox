package com.grighetti.pokemonbox.data.model

import com.google.gson.annotations.SerializedName

data class PokemonDetailResponse(
    val name: String,
    val height: Int,
    val weight: Int,
    val types: List<TypeWrapper>,
    val abilities: List<AbilityWrapper>,
    val stats: List<PokemonStat>,
    @SerializedName("sprites") val sprites: Sprite
)

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


data class Sprite(
    @SerializedName("front_default") val frontDefault: String?,
    @SerializedName("other") val other: OtherSprites?
)

data class OtherSprites(
    @SerializedName("official-artwork") val officialArtwork: OfficialArtwork?
)

data class OfficialArtwork(
    @SerializedName("front_default") val frontDefault: String?
)


data class PokemonStat(
    @SerializedName("base_stat") val baseStat: Int,
    val stat: NamedAPIResource
)

data class AbilityWrapper(val ability: NamedAPIResource)

data class TypeWrapper(val type: NamedAPIResource)
