package com.dogapp.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object para respuestas individuales de imagen de la API Dog CEO.
 */
data class DogResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)

/**
 * Data Transfer Object para la lista completa de razas de la API Dog CEO.
 */
data class DogBreedsResponse(
    @SerializedName("message")
    val message: Map<String, List<String>>,
    @SerializedName("status")
    val status: String
)
