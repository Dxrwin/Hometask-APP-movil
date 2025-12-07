package com.iub.hometask.features.auth.Login


import java.net.ConnectException
import java.net.SocketTimeoutException
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.iub.hometask.data.local.SessionManager
import com.iub.hometask.data.remote.RetrofitClient
import com.iub.hometask.data.repository.AuthRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Estados de la UI
sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val roleId: Int) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModel(
    private val repository: AuthRepositoryImpl
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _uiState.value = LoginUiState.Error("Por favor llena todos los campos")
            return
        }

        viewModelScope.launch {

            // 1. RESETEAR EL ESTADO PRIMERO (Truco para forzar recomposición)
            _uiState.value = LoginUiState.Idle

            _uiState.value = LoginUiState.Loading

            // Usamos un bloque try-catch explícito para capturar errores de red
            try {
                val result = repository.login(email, pass)

                result.fold(
                    onSuccess = {
                        val roleId = repository.getUserRole()
                        _uiState.value = LoginUiState.Success(roleId)
                    },
                    onFailure = { error ->
                        // Manejo inteligente del error
                        val friendlyMessage = when {
                            error.message?.contains("401") == true -> "Correo o contraseña incorrectos."
                            error.message?.contains("404") == true -> "Servidor no encontrado."
                            // Errores de conexión típicos
                            error.cause is ConnectException -> "No se pudo conectar al servidor. Verifica tu conexión a internet o la IP."
                            error.cause is SocketTimeoutException -> "El servidor tardó mucho en responder."
                            else -> "Error: ${error.message}"
                        }
                        _uiState.value = LoginUiState.Error(friendlyMessage)
                    }
                )
            } catch (e: Exception) {
                // Captura si el repositorio lanza la excepción en lugar de devolver Result.failure
                _uiState.value = LoginUiState.Error("Error de red: Verifica que el servidor esté activo.")
            }
        }
    }


    // --- FACTORY (ESTO EVITA QUE LA APP SE CIERRE) ---
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                // 1. Obtenemos contexto
                val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application)

                // 2. Creamos dependencias manualmente
                val api = RetrofitClient.authService
                val session = SessionManager(app.applicationContext)
                val repo = AuthRepositoryImpl(api, session)

                // 3. Devolvemos el ViewModel listo
                LoginViewModel(repo)
            }
        }
    }
}