package com.iub.hometask.features.members

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.iub.hometask.data.local.SessionManager
import com.iub.hometask.data.remote.RetrofitClient
import com.iub.hometask.data.repository.AuthRepositoryImpl
import com.iub.hometask.features.auth.Signup.SignUpUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddMemberViewModel(
    private val repository: AuthRepositoryImpl,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<SignUpUiState>(SignUpUiState.Idle)
    val uiState: StateFlow<SignUpUiState> = _uiState

    fun crearMiembro(
        nombre: String,
        email: String,
        pass: String,
        telefono: String,
        idRol: Int,
        idHogar: Int
    ) {
        // 1. Validar campos
        if (nombre.isBlank() || email.isBlank() || pass.isBlank()) {
            _uiState.value = SignUpUiState.Error("Completa todos los campos")
            return
        }

        // 2. VALIDACIÓN DE CONTRASEÑA (Solución al Error 422)
        if (pass.length < 8) {
            _uiState.value = SignUpUiState.Error("La contraseña debe tener al menos 8 caracteres")
            return
        }

        // 2. VALIDACIÓN DE TOKEN (Requisito clave)
        if (!sessionManager.isTokenValid()) {
            _uiState.value = SignUpUiState.Error("Tu sesión ha expirado. Por favor loguéate de nuevo.")
            return
        }

        viewModelScope.launch {
            _uiState.value = SignUpUiState.Loading

            // 3. Llamada al repositorio (El Interceptor inyectará el token automáticamente)
            val result = repository.registro(nombre, email, pass = pass, telefono = telefono, idRol = idRol, idHogar = idHogar)

            result.fold(
                onSuccess = {
                    _uiState.value = SignUpUiState.Success
                },
                onFailure = { error ->
                    _uiState.value = SignUpUiState.Error(error.message ?: "Error al crear miembro")
                }
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application)
                val context = app.applicationContext
                val session = SessionManager(context)

                // Usamos el cliente con seguridad
                val api = RetrofitClient.getAuthService(context)
                val repo = AuthRepositoryImpl(api, session)

                AddMemberViewModel(repo, session)
            }
        }
    }
}