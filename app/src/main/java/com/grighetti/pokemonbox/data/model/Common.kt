package com.grighetti.pokemonbox.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NamedAPIResource(
    @SerialName("name") val name: String,
    @SerialName("url") val url: String
)
