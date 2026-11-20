package com.dogapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogapp.data.remote.RetrofitClient
import com.dogapp.data.repository.DogRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel encargado de consultar información e imágenes de perros a través de Retrofit.
 */
class DogViewModel : ViewModel() {

    private val repository = DogRepository(RetrofitClient.dogApiService)

    private val _imageUrl = MutableLiveData<String?>()
    val imageUrl: LiveData<String?> = _imageUrl

    private val _breedsList = MutableLiveData<List<String>>()
    val breedsList: LiveData<List<String>> = _breedsList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun fetchRandomDogImage() {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getRandomDogImage()
                if (response.isSuccessful && response.body() != null) {
                    _imageUrl.postValue(response.body()?.message)
                } else {
                    _errorMessage.postValue("Error al obtener la imagen de perro")
                }
            } catch (e: Exception) {
                _errorMessage.postValue(e.localizedMessage ?: "Error de conexión")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun fetchDogImageByBreed(breed: String) {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getRandomImageByBreed(breed.lowercase())
                if (response.isSuccessful && response.body() != null) {
                    _imageUrl.postValue(response.body()?.message)
                } else {
                    _errorMessage.postValue("No se encontró imagen para la raza $breed")
                }
            } catch (e: Exception) {
                _errorMessage.postValue(e.localizedMessage ?: "Error de conexión")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun fetchAllBreeds() {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getAllBreeds()
                if (response.isSuccessful && response.body() != null) {
                    val breeds = response.body()?.message?.keys?.toList() ?: emptyList()
                    _breedsList.postValue(breeds)
                } else {
                    _errorMessage.postValue("Error al consultar lista de razas")
                }
            } catch (e: Exception) {
                _errorMessage.postValue(e.localizedMessage ?: "Error de conexión")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}
