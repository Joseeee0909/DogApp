package com.dogapp.data.repository

import com.dogapp.data.remote.api.DogApiService

/**
 * Repositorio que encapsula las llamadas a la API Dog CEO
 * proporcionando manejo de errores de red y HTTP.
 */
class DogRepository(private val dogApiService: DogApiService) {

    /**
     * Obtiene la lista completa de razas de perros.
     */
    suspend fun getBreedsList(): Result<List<String>> {
        return try {
            val response = dogApiService.getAllBreeds()
            if (response.isSuccessful && response.body()?.status == "success") {
                val breedsMap = response.body()?.message ?: emptyMap()
                Result.success(breedsMap.keys.toList())
            } else {
                Result.failure(Exception("Error HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene una URL de imagen aleatoria para una raza específica.
     */
    suspend fun getRandomImageByBreed(breed: String): Result<String> {
        return try {
            val formattedBreed = breed.trim().lowercase()
            val response = dogApiService.getRandomImageByBreed(formattedBreed)
            if (response.isSuccessful && response.body()?.status == "success") {
                val imageUrl = response.body()?.message
                if (!imageUrl.isNullOrEmpty()) {
                    Result.success(imageUrl)
                } else {
                    Result.failure(Exception("Respuesta vacía de la API"))
                }
            } else {
                Result.failure(Exception("Raza no encontrada o error HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene una URL de imagen aleatoria de cualquier perro.
     */
    suspend fun getRandomDogImage(): Result<String> {
        return try {
            val response = dogApiService.getRandomDogImage()
            if (response.isSuccessful && response.body()?.status == "success") {
                val imageUrl = response.body()?.message
                if (!imageUrl.isNullOrEmpty()) {
                    Result.success(imageUrl)
                } else {
                    Result.failure(Exception("Respuesta vacía de la API"))
                }
            } else {
                Result.failure(Exception("Error HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
