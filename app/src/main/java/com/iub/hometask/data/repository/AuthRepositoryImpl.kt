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
                val roleId = data.rol?.id ?: 2

                // ACTUALIZACIÓN: Guardamos ID de miembro y Hogar también
                sessionManager.saveSession(
                    token = data.accessToken,
                    roleId = roleId,
                    userId = data.idMiembro, // Viene del TokenResponseDto
                    id_hogar = data.idhogar   // Viene del TokenResponseDto
                )

                Result.success(true)
            } else {
                Result.failure(Exception("Error Login: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // REGISTRO: Solo llama a la API, NO guarda sesión
    override suspend fun registro(
        nombre: String,
        correo: String,
        pass: String,
        telefono: String,
        idRol: Int,
        idHogar: Int
    ): Result<Boolean> {
        return try {
            val request = RegisterRequestDto(
                nombreCompleto = nombre,
                correoElectronico = correo,
                contrasena = pass,
                telefono = telefono,
                idRol = idRol,
                idHogar = idHogar
            )
            // ... llamada a la api (igual que antes) ...
            val response = api.registrar(request)

            if (response.isSuccessful) {
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