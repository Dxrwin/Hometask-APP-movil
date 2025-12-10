package com.iub.hometask.domain.repository

interface AuthRepository {
    suspend fun login(correo: String, pass: String): Result<Boolean>

    // Actualizamos la firma para recibir rol y hogar
    suspend fun registro(
        nombre: String,
        correo: String,
        pass: String,
        telefono: String,
        idRol: Int,
        idHogar: Int
    ): Result<Boolean>

    fun getUserRole(): Int
}