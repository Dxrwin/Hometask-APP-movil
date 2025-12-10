package com.iub.hometask.features.gallery

import android.app.Application
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.iub.hometask.data.repository.GalleryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class GalleryUiState {
    object Loading : GalleryUiState()
    data class Success(val photos: List<Uri>) : GalleryUiState()
    object Empty : GalleryUiState()
}

class GalleryViewModel(
    private val repository: GalleryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<GalleryUiState>(GalleryUiState.Loading)
    val uiState: StateFlow<GalleryUiState> = _uiState

    // Cargar fotos (se llama al iniciar y al volver de la cámara)
    fun loadPhotos() {
        viewModelScope.launch {
            _uiState.value = GalleryUiState.Loading
            val photos = repository.getAppPhotos()
            if (photos.isEmpty()) {
                _uiState.value = GalleryUiState.Empty
            } else {
                _uiState.value = GalleryUiState.Success(photos)
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application)
                val repository = GalleryRepository(app.applicationContext)
                GalleryViewModel(repository)
            }
        }
    }
}