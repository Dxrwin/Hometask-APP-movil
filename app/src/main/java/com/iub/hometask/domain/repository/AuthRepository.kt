package com.iub.hometask.domain.repository

interface AuthRepository {
    // Cambio: Ahora devuelven Result<Boolean> porque el dato (Token) se gestiona internamente.
    suspend fun login(correo: String, pass: String): Result<Boolean>

    suspend fun registro(nombre: String, correo: String, pass: String): Result<Boolean>

    // Nuevo: Necesitamos exponer esto para que el ViewModel sepa a dónde navegar
    fun getUserRole(): Int
}