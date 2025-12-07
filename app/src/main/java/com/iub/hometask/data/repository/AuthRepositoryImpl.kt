package com.iub.hometask.data.repository

import com.iub.hometask.data.dto.auth.LoginRequestDto
import com.iub.hometask.data.dto.auth.RegisterRequestDto
import com.iub.hometask.data.local.SessionManager
import com.iub.hometask.data.remote.api.AuthApiService
import com.iub.hometask.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val api: AuthApiService,
    private val sessionManager: SessionManager // Inyectamos el SessionManager
) : AuthRepository {

    // LOGIN: Guarda en caché si es exitoso
    override suspend fun login(correo: String, pass: String): Result<Boolean> {
        return try {
            val request = LoginRequestDto(correoElectronico = correo, contrasena = pass)
            val response = api.login(request)

            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                // LOGICA: Guardar Token y Rol en Cache
                val roleId = data.rol?.id ?: 2 // Default a 2 (Miembro) si viene null
                sessionManager.saveAuthToken(data.accessToken, roleId)

                Result.success(true)
            } else {
                Result.failure(Exception("Error Login: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // REGISTRO: Solo llama a la API, NO guarda sesión
    override suspend fun registro(nombre: String, correo: String, pass: String): Result<Boolean> {
        return try {
            val request = RegisterRequestDto(
                nombreCompleto = nombre,
                correoElectronico = correo,
                contrasena = pass,
                idRol = 2 // Por defecto registramos miembros, el admin se crea manual o por DB
            )
            val response = api.registrar(request)

            if (response.isSuccessful) {
                // LOGICA: No guardamos nada. El usuario debe ir al Login manualmente.
                Result.success(true)
            } else {
                Result.failure(Exception("Error Registro: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getUserRole(): Int {
        return sessionManager.getUserRole()
    }
}