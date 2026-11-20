package com.dogapp.data.remote.api

import com.dogapp.data.remote.dto.DogBreedsResponse
import com.dogapp.data.remote.dto.DogResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Interfaz de Retrofit para consultar endpoints de Dog CEO API.
 */
interface DogApiService {

    @GET("breeds/image/random")
    suspend fun getRandomDogImage(): Response<DogResponse>

    @GET("breed/{breed}/images/random")
    suspend fun getRandomImageByBreed(@Path("breed") breed: String): Response<DogResponse>

    @GET("breeds/list/all")
    suspend fun getAllBreeds(): Response<DogBreedsResponse>
}
