package com.grighetti.pokemonbox.data.model

import android.util.Log
import com.grighetti.pokemonbox.utils.Utils
import com.google.gson.annotations.SerializedName
import java.util.Locale

data class EvolutionChainReference(val url: String)

data class EvolutionChainResponse(val chain: EvolutionChainLink)

data class EvolutionChainLink(
    val species: NamedAPIResource,
    @SerializedName("evolves_to") val evolvesTo: List<EvolutionChainLink>,
    @SerializedName("evolution_details") val evolutionDetails: List<EvolutionDetail> = emptyList()
)

data class EvolutionDetail(
    @SerializedName("min_level") val minLevel: Int?,
    val trigger: NamedAPIResource?,
    val item: NamedAPIResource?,
    val gender: Int?,
    @SerializedName("held_item") val heldItem: NamedAPIResource?,
    @SerializedName("known_move") val knownMove: NamedAPIResource?,
    @SerializedName("known_move_type") val knownMoveType: NamedAPIResource?,
    val location: NamedAPIResource?,
    @SerializedName("min_affection") val minAffection: Int?,  // 🔹 Affection (per Sylveon)
    @SerializedName("min_beauty") val minBeauty: Int?,        // 🔹 Beauty (per Feebas)
    @SerializedName("min_happiness") val minHappiness: Int?,  // 🔹 Happiness (per Espeon e Umbreon)
    @SerializedName("needs_overworld_rain") val needsOverworldRain: Boolean,
    @SerializedName("party_species") val partySpecies: NamedAPIResource?,
    @SerializedName("party_type") val partyType: NamedAPIResource?,
    @SerializedName("relative_physical_stats") val relativePhysicalStats: Int?,
    @SerializedName("time_of_day") val timeOfDay: String,     // 🔹 Momento della giornata (per Espeon e Umbreon)
    @SerializedName("trade_species") val tradeSpecies: NamedAPIResource?,
    @SerializedName("turn_upside_down") val turnUpsideDown: Boolean // 🔹 Per Malamar (deve essere capovolto)
)


fun EvolutionChainResponse.extractEvolutionChain(): List<EvolutionStage> {
    val evolutionStages = mutableListOf<EvolutionStage>()

    fun extractStages(chain: EvolutionChainLink, parentName: String? = null) {
        val id = Utils.extractIdFromUrl(chain.species.url)

        val evolutionDetail = chain.evolutionDetails.firstOrNull()

        val evolutionLevel = evolutionDetail?.minLevel ?: -1

        val evolutionTrigger = evolutionDetail?.trigger?.name ?: "Unknown"
        val evolutionItem = evolutionDetail?.item?.name?.replace("-", " ")?.capitalize(Locale.ROOT)

        Log.d(
            "EvolutionChain",
            "🔄 Processing: ${chain.species.name} ➡ MinLevel: $evolutionLevel | Trigger: $evolutionTrigger"
        )

        val evolutionMethodString = when (evolutionTrigger) {
            "level-up" -> if (evolutionLevel > 0) "Level $evolutionLevel" else "Level Up"
            "use-item" -> "Use $evolutionItem"
            "trade" -> "Trade"
            else -> "Unknown Evolution Method"
        }

        evolutionStages.add(
            EvolutionStage(
                name = chain.species.name.replaceFirstChar { it.uppercase() },
                level = evolutionLevel,
                imageUrl = Utils.getPokemonImageUrl(id),
                evolutionMethod = evolutionMethodString
            )
        )

        chain.evolvesTo.forEach { nextStage ->
            extractStages(nextStage, chain.species.name)
        }
    }

    extractStages(this.chain)
    return evolutionStages
}


fun EvolutionChainResponse.extractEvolutionChainMin(): List<EvolutionStage> {
    val evolutionStages = mutableListOf<EvolutionStage>()

    fun extractStages(chain: EvolutionChainLink, method: String = "Unknown") {
        val id = Utils.extractIdFromUrl(chain.species.url)

        val evolutionDetail = chain.evolutionDetails.firstOrNull()
        val evolutionLevel = evolutionDetail?.minLevel
        val evolutionTrigger = evolutionDetail?.trigger?.name ?: "Unknown"

        val displayLevel = evolutionLevel?.toString() ?: "—"
        val displayMethod = formatEvolutionMethod(evolutionTrigger, evolutionLevel)

        evolutionStages.add(
            EvolutionStage(
                name = chain.species.name,
                level = evolutionLevel ?: -1,
                imageUrl = Utils.getPokemonImageUrl(id),
                evolutionMethod = displayMethod
            )
        )

        // Processa le evoluzioni successive
        chain.evolvesTo.forEach { nextStage ->
            extractStages(nextStage)
        }
    }

    extractStages(this.chain)

    return evolutionStages
}


fun formatEvolutionMethod(method: String, level: Int?): String {
    return when {
        method.equals("level-up", ignoreCase = true) ->
            if (level != null && level > 0) "Evolves at Level $level" else "Evolves by Leveling Up"

        method.equals("trade", ignoreCase = true) -> "Evolves via Trading"
        method.equals("use-item", ignoreCase = true) -> "Evolves using an Item"
        method.equals("shed", ignoreCase = true) -> "Evolves via Shedding"
        method.equals("spin", ignoreCase = true) -> "Evolves by Spinning"
        method.equals("tower-of-darkness", ignoreCase = true) -> "Evolves in the Tower of Darkness"
        method.equals("tower-of-waters", ignoreCase = true) -> "Evolves in the Tower of Waters"
        else -> "Unknown Evolution Method"
    }
}


// Modello aggiornato di EvolutionStage con il metodo di evoluzione
data class EvolutionStage(
    val name: String,
    val level: Int?,
    val imageUrl: String,
    val evolutionMethod: String // ✅ Aggiunto il metodo evolutivo
)


