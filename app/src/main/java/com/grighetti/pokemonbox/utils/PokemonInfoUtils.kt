package com.grighetti.pokemonbox.utils

object PokemonInfoUtils {

    /**
     * Converts height from meters to feet and inches.
     */
    fun formatHeight(heightInMeters: Double): String {
        val inches = (heightInMeters * 39.37).toInt()
        val feet = inches / 12
        val remainingInches = inches % 12
        return "${feet}'${remainingInches}\" (${heightInMeters}m)"
    }

    /**
     * Converts weight from kilograms to pounds.
     */
    fun formatWeight(weightInKg: Double): String {
        val pounds = weightInKg * 2.20462
        return "%.1f lbs (%.1f kg)".format(pounds, weightInKg)
    }

    /**
     * Formats Pokémon stat names into a readable format.
     */
    fun formatStatName(stat: String): String {
        return when (stat.lowercase()) {
            "special-attack" -> "Sp. Atk"
            "special-defense" -> "Sp. Def"
            "hp" -> "HP"
            else -> stat.split("-")
                .joinToString(" ") { it.lowercase().replaceFirstChar { ch -> ch.uppercase() } }
        }
    }

    /**
     * Calculates the gender ratio of a Pokémon based on its gender rate.
     * Returns a pair representing the percentage of male and female Pokémon.
     *
     * @param genderRate The gender rate from the Pokémon API.
     * @return A pair of doubles representing the male and female ratio.
     */
    fun calculateGenderRatio(genderRate: Int): Pair<Double, Double> {
        return when (genderRate) {
            -1 -> Pair(0.0, 0.0)
            0 -> Pair(100.0, 0.0)
            1 -> Pair(87.5, 12.5)
            2 -> Pair(75.0, 25.0)
            3 -> Pair(62.5, 37.5)
            4 -> Pair(50.0, 50.0)
            5 -> Pair(37.5, 62.5)
            6 -> Pair(25.0, 75.0)
            7 -> Pair(12.5, 87.5)
            8 -> Pair(0.0, 100.0)
            else -> Pair(0.0, 0.0)
        }
    }

    /**
     * Extracts the Pokémon ID from a given URL.
     *
     * @param url The Pokémon API URL.
     * @return The Pokémon's ID as an integer.
     */
    fun extractIdFromUrl(url: String): Int {
        return url.split("/").dropLast(1).last().toInt()
    }

    /**
     * Generates the URL for a Pokémon's official artwork.
     *
     * @param pokemonId The Pokémon's ID.
     * @return The image URL.
     */
    fun getPokemonImageUrl(pokemonId: Int): String {
        return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$pokemonId.png"
    }
}
