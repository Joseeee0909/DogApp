package com.dogapp.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object para las respuestas de la API Dog CEO.
 */
data class DogResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)

/**
 * DTO para listar razas de perros.
 */
data class DogBreedsResponse(
    @SerializedName("message")
    val message: Map<String, List<String>>,
    @SerializedName("status")
    val status: String
)
