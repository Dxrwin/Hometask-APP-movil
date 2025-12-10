package com.iub.hometask.data.repository

import android.util.Log
import com.iub.hometask.data.remote.api.MemberApiService

class MemberRepository(private val api: MemberApiService) {

    // Cambia esto por tu IP base (debe coincidir con RetrofitClient)
    private val BASE_URL_IMAGES = "http://192.168.12.159:8000/static"

    private fun buildImageUrl(path: String?): String? {
        if (path.isNullOrBlank()) return null
        val trimmed = path.removePrefix("/").removeSuffix(" ")
        // Evita dobles slashes
        return "$BASE_URL_IMAGES/$trimmed"
    }

    suspend fun getMembers(): Result<List<MemberUiModel>> = runCatching {
        val resp = api.getAllMembers()
        if (!resp.isSuccessful || resp.body() == null) {
            error("Error: ${resp.code()}")
        }
        resp.body()!!.map { dto ->
            val fullImageUrl = buildImageUrl(dto.imagenPerfil)
            Log.d("IMG_URL", "Member ${dto.id}: $fullImageUrl")
            MemberUiModel(
                id = dto.id,
                name = dto.nombreCompleto,
                email = dto.correoElectronico,
                roleName = dto.rol?.nombre ?: "Miembro",
                imageUrl = fullImageUrl
            )
        }
    }
}

// Modelo simplificado para la UI
data class MemberUiModel(
    val id: Int,
    val name: String,
    val email: String,
    val roleName: String,
    val imageUrl: String?
)