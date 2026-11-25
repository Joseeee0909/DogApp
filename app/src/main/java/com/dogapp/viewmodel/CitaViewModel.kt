package com.dogapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dogapp.data.local.database.AppDatabase
import com.dogapp.data.local.entity.Cita
import com.dogapp.data.repository.CitaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel encargado de la gestión de Citas (P4-05).
 */
class CitaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CitaRepository
    val allCitas: LiveData<List<Cita>>

    private val _selectedCita = MutableLiveData<Cita?>()
    val selectedCita: LiveData<Cita?> = _selectedCita

    private val _operationStatus = MutableLiveData<Boolean?>()
    val operationStatus: LiveData<Boolean?> = _operationStatus

    init {
        val citaDao = AppDatabase.getDatabase(application).citaDao()
        repository = CitaRepository(citaDao)
        allCitas = repository.allCitas
    }

    fun getCitaById(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val cita = repository.getCitaById(id)
            _selectedCita.postValue(cita)
        }
    }

    fun insertCitaWithDynamicImage(
        nombreMascota: String,
        raza: String,
        nombrePropietario: String,
        telefono: String,
        fecha: String,
        hora: String,
        sintoma: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.insertCitaWithDynamicImage(
                    nombreMascota, raza, nombrePropietario, telefono, fecha, hora, sintoma
                )
                _operationStatus.postValue(true)
            } catch (e: Exception) {
                _operationStatus.postValue(false)
            }
        }
    }

    fun insertCita(cita: Cita) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.insertCita(cita)
                _operationStatus.postValue(true)
            } catch (e: Exception) {
                _operationStatus.postValue(false)
            }
        }
    }

    fun updateCita(cita: Cita) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.updateCita(cita)
                _operationStatus.postValue(true)
            } catch (e: Exception) {
                _operationStatus.postValue(false)
            }
        }
    }

    /**
     * P5-05: actualiza la cita en Room (mismo ID) y renueva la imagen si cambió la raza.
     */
    fun updateCitaWithDynamicImage(cita: Cita, razaAnterior: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.updateCitaWithDynamicImage(cita, razaAnterior)
                _operationStatus.postValue(true)
            } catch (e: Exception) {
                _operationStatus.postValue(false)
            }
        }
    }

    fun deleteCita(cita: Cita) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.deleteCita(cita)
                _operationStatus.postValue(true)
            } catch (e: Exception) {
                _operationStatus.postValue(false)
            }
        }
    }

    fun resetOperationStatus() {
        _operationStatus.value = null
    }
}
