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
 * ViewModel encargado de gestionar los datos obtenidos de la API Dog CEO.
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
            val result = repository.getRandomDogImage()
            result.onSuccess { url ->
                _imageUrl.postValue(url)
                _errorMessage.postValue(null)
            }.onFailure { exception ->
                _errorMessage.postValue(exception.localizedMessage ?: "Error de red")
            }
            _isLoading.postValue(false)
        }
    }

    fun fetchDogImageByBreed(breed: String) {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getRandomImageByBreed(breed)
            result.onSuccess { url ->
                _imageUrl.postValue(url)
                _errorMessage.postValue(null)
            }.onFailure { exception ->
                _errorMessage.postValue(exception.localizedMessage ?: "Raza no encontrada")
            }
            _isLoading.postValue(false)
        }
    }

    fun fetchAllBreeds() {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getBreedsList()
            result.onSuccess { breeds ->
                _breedsList.postValue(breeds)
                _errorMessage.postValue(null)
            }.onFailure { exception ->
                _errorMessage.postValue(exception.localizedMessage ?: "Error al cargar razas")
            }
            _isLoading.postValue(false)
        }
    }
}
