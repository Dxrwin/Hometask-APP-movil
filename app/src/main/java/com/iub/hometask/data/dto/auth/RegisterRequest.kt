package com.iub.hometask.data.dto.auth

import com.google.gson.annotations.SerializedName

data class RegisterRequestDto(
    @SerializedName("nombre_completo") val nombreCompleto: String,
    @SerializedName("correo_electronico") val correoElectronico: String,
    @SerializedName("contrasena") val contrasena: String,
    // Campo NUEVO requerido por tu JSON
    @SerializedName("telefono") val telefono: String,
    @SerializedName("id_rol") val idRol: Int,
    @SerializedName("id_hogar") val idHogar: Int
)