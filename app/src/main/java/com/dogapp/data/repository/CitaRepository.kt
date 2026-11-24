package com.dogapp.data.repository

import androidx.lifecycle.LiveData
import com.dogapp.data.local.dao.CitaDao
import com.dogapp.data.local.entity.Cita
import com.dogapp.data.remote.RetrofitClient
import com.dogapp.data.remote.api.DogApiService

/**
 * Repositorio que abstrae la fuente de datos local (Room) y remota (Dog CEO API) para Citas (P4-05).
 */
class CitaRepository(
    private val citaDao: CitaDao,
    private val dogApiService: DogApiService = RetrofitClient.dogApiService
) {

    val allCitas: LiveData<List<Cita>> = citaDao.getAllCitas()

    suspend fun getCitaById(id: Int): Cita? {
        return citaDao.getCitaById(id)
    }

    /**
     * Consulta la imagen aleatoria de la raza vía Retrofit (con fallback a imagen aleatoria general)
     * e inserta la cita en la base de datos Room mediante Corrutinas (Dispatchers.IO).
     */
    suspend fun insertCitaWithDynamicImage(
        nombreMascota: String,
        raza: String,
        nombrePropietario: String,
        telefono: String,
        fecha: String,
        hora: String,
        sintoma: String
    ): Long {
        val imageUrl = try {
            val formattedBreed = raza.trim().lowercase()
            val breedResponse = dogApiService.getRandomImageByBreed(formattedBreed)
            if (breedResponse.isSuccessful && breedResponse.body()?.status == "success") {
                breedResponse.body()?.message
            } else {
                val randomResponse = dogApiService.getRandomDogImage()
                if (randomResponse.isSuccessful && randomResponse.body()?.status == "success") {
                    randomResponse.body()?.message
                } else null
            }
        } catch (e: Exception) {
            try {
                val randomResponse = dogApiService.getRandomDogImage()
                if (randomResponse.isSuccessful && randomResponse.body()?.status == "success") {
                    randomResponse.body()?.message
                } else null
            } catch (ex: Exception) {
                null
            }
        }

        val cita = Cita(
            nombreMascota = nombreMascota,
            raza = raza,
            propietario = nombrePropietario,
            telefono = telefono,
            fecha = fecha,
            hora = hora,
            motivo = sintoma,
            imagenUrl = imageUrl,
            estado = "Pendiente"
        )
        return citaDao.insertCita(cita)
    }

    suspend fun insertCita(cita: Cita): Long {
        return citaDao.insertCita(cita)
    }

    suspend fun updateCita(cita: Cita): Int {
        return citaDao.updateCita(cita)
    }

    suspend fun deleteCita(cita: Cita): Int {
        return citaDao.deleteCita(cita)
    }

    suspend fun deleteCitaById(id: Int): Int {
        return citaDao.deleteCitaById(id)
    }
}
