package com.iub.hometask.data.dto.auth
import com.google.gson.annotations.SerializedName

data class TokenResponseDto(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("id_miembro") val idMiembro: Int,
    @SerializedName("idhogar") val idhogar: Int,
    @SerializedName("rol") val rol: RolDto? // Objeto Rol completo
)