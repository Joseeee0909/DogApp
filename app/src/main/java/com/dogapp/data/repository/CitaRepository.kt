package com.dogapp.data.repository

import androidx.lifecycle.LiveData
import com.dogapp.data.local.dao.CitaDao
import com.dogapp.data.local.entity.Cita

/**
 * Repositorio que abstrae la fuente de datos local de citas (Room).
 */
class CitaRepository(private val citaDao: CitaDao) {

    val allCitas: LiveData<List<Cita>> = citaDao.getAllCitas()

    suspend fun getCitaById(id: Int): Cita? {
        return citaDao.getCitaById(id)
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
