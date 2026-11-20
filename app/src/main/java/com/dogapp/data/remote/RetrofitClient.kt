package com.dogapp.data.remote

import com.dogapp.data.remote.api.DogApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Cliente Retrofit para instanciar el servicio de la API Dog CEO.
 */
object RetrofitClient {

    private const val BASE_URL = "https://dog.ceo/api/"

    val dogApiService: DogApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DogApiService::class.java)
    }
}
