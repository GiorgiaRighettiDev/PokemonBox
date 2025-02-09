package com.grighetti.pokemonbox.data.domain


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