package com.grighetti.pokemonbox.di

import com.grighetti.pokemonbox.data.remote.PokeApiService
import com.grighetti.pokemonbox.data.repository.PokemonRepository
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val BASE_URL = "https://pokeapi.co/api/v2/"

    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true // Evita crash se ci sono campi non mappati nel JSON
            coerceInputValues = true // Converte automaticamente i valori di input null o mancanti
            isLenient = true         // Permette JSON con una formattazione meno rigida
            encodeDefaults = true
        }
    }

    @Provides
    @Singleton
    fun provideRetrofit(json: Json): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType())) // ✅ Usato Kotlinx Serialization
            .build()
    }

    @Provides
    @Singleton
    fun providePokeApiService(retrofit: Retrofit): PokeApiService {
        return retrofit.create(PokeApiService::class.java)
    }

    @Provides
    @Singleton
    fun providePokemonRepository(api: PokeApiService): PokemonRepository {
        return PokemonRepository(api)
    }
}
