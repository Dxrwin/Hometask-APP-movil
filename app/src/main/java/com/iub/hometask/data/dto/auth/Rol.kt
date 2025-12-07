package com.iub.hometask.data.dto.auth
import com.google.gson.annotations.SerializedName

data class RolDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre_rol") val nombreRol: String, // Backend usa "nombre_rol" en RolBase
    @SerializedName("descripcion") val descripcion: String?
)