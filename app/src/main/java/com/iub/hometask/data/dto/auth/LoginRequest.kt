package com.iub.hometask.data.dto.auth

import com.google.gson.annotations.SerializedName

data class LoginRequestDto(
    // IMPORTANTE: El backend espera 'correo_electronico', no 'email'
    @SerializedName("correo_electronico") val correoElectronico: String,
    @SerializedName("contrasena") val contrasena: String
)