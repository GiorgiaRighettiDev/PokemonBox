package com.grighetti.pokemonbox.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a generic API resource reference with a name and URL.
 * Many Pokémon API endpoints use this format for linking related entities.
 *
 * @property name The name of the referenced resource.
 * @property url The API endpoint URL for this resource.
 */
@Serializable
data class NamedAPIResource(
    @SerialName("name") val name: String,
    @SerialName("url") val url: String
)
