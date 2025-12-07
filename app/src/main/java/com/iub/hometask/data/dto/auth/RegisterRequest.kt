package com.iub.hometask.data.dto.auth

import com.google.gson.annotations.SerializedName

data class RegisterRequestDto(
    @SerializedName("nombre_completo") val nombreCompleto: String,
    @SerializedName("correo_electronico") val correoElectronico: String,
    @SerializedName("contrasena") val contrasena: String,
    // El backend requiere un ID de rol (ej: 1 para Admin, 2 para Miembro)
    @SerializedName("id_rol") val idRol: Int = 2,
    // Opcional en el backend, por si se une a un hogar existente o crea uno nuevo
    @SerializedName("id_hogar") val idHogar: Int? = null
)