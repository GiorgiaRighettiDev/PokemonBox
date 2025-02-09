package com.grighetti.pokemonbox.data.domain

/**
 * Represents a single stage in a Pokémon's evolution chain.
 *
 * @property name The Pokémon's name.
 * @property level The required level for evolution (if applicable).
 * @property imageUrl URL of the Pokémon's sprite.
 * @property evolutionMethod The method by which this Pokémon evolves (e.g., level, item).
 */
data class EvolutionStage(
    val name: String,
    val level: Int? = null,
    val imageUrl: String,
    val evolutionMethod: String
)

