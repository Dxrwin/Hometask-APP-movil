package com.iub.hometask.features.auth.Signup

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
//import androidx.lifecycle.viewmodel.creation.InitializerViewModelFactoryBuilder
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.iub.hometask.data.local.SessionManager
import com.iub.hometask.data.remote.RetrofitClient
import com.iub.hometask.data.repository.AuthRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// 1. Definición de los estados de la pantalla
sealed class SignUpUiState {
    object Idle : SignUpUiState()
    object Loading : SignUpUiState()
    object Success : SignUpUiState()
    data class Error(val message: String) : SignUpUiState()
}

class SignUpViewModel(
    private val repository: AuthRepositoryImpl
) : ViewModel() {

    // 2. Declaración de las variables de estado (CORRECCIÓN DEL ERROR)
    private val _uiState = MutableStateFlow<SignUpUiState>(SignUpUiState.Idle)
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    // 3. Lógica de registro
    fun registrarse(nombre: String, email: String, pass: String, confirmPass: String) {
        // Validaciones básicas antes de llamar al backend
        if (nombre.isBlank() || email.isBlank() || pass.isBlank()) {
            _uiState.value = SignUpUiState.Error("Por favor completa todos los campos")
            return
        }

        if (pass != confirmPass) {
            _uiState.value = SignUpUiState.Error("Las contraseñas no coinciden")
            return
        }

        viewModelScope.launch {
            _uiState.value = SignUpUiState.Loading

            // Llamada al repositorio
            val result = repository.registro(nombre, email, pass)

            result.fold(
                onSuccess = {
                    // ÉXITO: El usuario debe ir al login, no guardamos token aquí
                    _uiState.value = SignUpUiState.Success
                },
                onFailure = { error ->
                    _uiState.value = SignUpUiState.Error(error.message ?: "Error desconocido")
                }
            )
        }
    }

    // 4. Factory para poder inyectar el Repositorio automáticamente
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                // Obtenemos el contexto de la aplicación para el SessionManager
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application)
                val context = application.applicationContext

                // Creamos las dependencias
                val api = RetrofitClient.authService
                val sessionManager = SessionManager(context)
                val repository = AuthRepositoryImpl(api, sessionManager)

                // Retornamos el ViewModel con sus dependencias listas
                SignUpViewModel(repository)
            }
        }
    }
}