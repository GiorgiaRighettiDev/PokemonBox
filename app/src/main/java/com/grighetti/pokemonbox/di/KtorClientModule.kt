package com.grighetti.pokemonbox.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

/**
 * Provides dependencies for the Ktor HTTP client.
 */
@Module
@InstallIn(SingletonComponent::class)
object KtorClientModule {

    /**
     * Provides the base URL for API requests.
     *
     * @return The base API URL as a string.
     */
    @Provides
    @Singleton
    fun provideBaseUrl(): String = "https://pokeapi.co/api/v2/"

    /**
     * Provides a singleton instance of [HttpClient] configured for API requests.
     *
     * @return Configured Ktor [HttpClient] instance.
     */
    @Provides
    @Singleton
    fun provideKtorClient(): HttpClient {
        return HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true // Ignores unknown JSON keys to prevent errors.
                    coerceInputValues = true // Allows missing fields to use default values.
                    prettyPrint = false // Keeps JSON minimal for efficiency.
                })
            }
            install(Logging) {
                level = LogLevel.INFO // Reduce logging for production.
            }
        }
    }
}
