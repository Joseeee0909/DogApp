package com.dogapp.data.repository

import com.dogapp.data.remote.api.DogApiService
import com.dogapp.data.remote.dto.DogBreedsResponse
import com.dogapp.data.remote.dto.DogResponse
import retrofit2.Response

/**
 * Repositorio que abstrae las llamadas de red a la API de Dog CEO.
 */
class DogRepository(private val dogApiService: DogApiService) {

    suspend fun getRandomDogImage(): Response<DogResponse> {
        return dogApiService.getRandomDogImage()
    }

    suspend fun getRandomImageByBreed(breed: String): Response<DogResponse> {
        return dogApiService.getRandomImageByBreed(breed)
    }

    suspend fun getAllBreeds(): Response<DogBreedsResponse> {
        return dogApiService.getAllBreeds()
    }
}
