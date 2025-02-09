package com.grighetti.pokemonbox.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the detailed response for a Pokémon from the API.
 *
 * @property name The Pokémon's name.
 * @property height The Pokémon's height in decimeters (1 dm = 0.1 meters).
 * @property weight The Pokémon's weight in hectograms (1 hg = 0.1 kg).
 * @property types The types assigned to the Pokémon.
 * @property abilities The list of abilities the Pokémon can have.
 * @property stats The base stats of the Pokémon.
 * @property sprites The available sprite images for the Pokémon.
 */
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

/**
 * Represents the Pokémon's sprite images.
 *
 * @property frontDefault The default front-facing sprite URL.
 * @property other Additional sprite variations, including official artwork.
 */
@Serializable
data class Sprite(
    @SerialName("front_default") val frontDefault: String?,
    @SerialName("other") val other: OtherSprites? = null
)

/**
 * Contains additional sprite variations.
 *
 * @property officialArtwork The official artwork sprite.
 */
@Serializable
data class OtherSprites(
    @SerialName("official-artwork") val officialArtwork: OfficialArtwork? = null
)

/**
 * Represents the official artwork of a Pokémon.
 *
 * @property frontDefault The official artwork URL.
 */
@Serializable
data class OfficialArtwork(
    @SerialName("front_default") val frontDefault: String?
)

/**
 * Represents a Pokémon's individual stat.
 *
 * @property baseStat The base value of the stat.
 * @property stat The stat name and reference.
 */
@Serializable
data class PokemonStat(
    @SerialName("base_stat") val baseStat: Int,
    @SerialName("stat") val stat: NamedAPIResource
)

/**
 * Wraps a Pokémon ability reference.
 *
 * @property ability The ability's name and reference.
 */
@Serializable
data class AbilityWrapper(
    @SerialName("ability") val ability: NamedAPIResource
)

/**
 * Wraps a Pokémon type reference.
 *
 * @property type The type's name and reference.
 */
@Serializable
data class TypeWrapper(
    @SerialName("type") val type: NamedAPIResource
)
