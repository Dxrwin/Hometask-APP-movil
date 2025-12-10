package com.iub.hometask.data.dto.member

import com.google.gson.annotations.SerializedName

data class MemberResponseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre_completo") val nombreCompleto: String,
    @SerializedName("correo_electronico") val correoElectronico: String,
    @SerializedName("telefono") val telefono: String?,
    @SerializedName("imagen_perfil") val imagenPerfil: String?, // Puede ser null
    @SerializedName("id_rol") val idRol: Int,
    @SerializedName("rol") val rol: MemberRoleDto?
)

data class MemberRoleDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("descripcion") val descripcion: String?
)