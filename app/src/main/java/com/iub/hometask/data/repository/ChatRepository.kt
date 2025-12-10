package com.iub.hometask.data.repository

import com.iub.hometask.data.dto.chat.MessageCreateRequestDto
import com.iub.hometask.data.local.SessionManager
import com.iub.hometask.data.remote.api.ChatApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

data class ChatMessage(
    val id: Int,
    val content: String,
    val isMine: Boolean, // Calculado comparando idRemitente con mi ID
    val timestamp: String,
    val imageUrl: String?,
    val isRead: Boolean
)

class ChatRepository(
    private val api: ChatApiService,
    private val sessionManager: SessionManager,
    private val baseUrlStatic: String = "http://192.168.2.13:8000/static/" // TU IP AQUÍ
) {

    // Obtener mi ID guardado en sesión (asumiendo que lo guardaste al login)
    // Si no lo tienes en SessionManager, deberías guardarlo al hacer Login
    private val myMemberId: Int
        get() = sessionManager.getUserId() // Necesitas implementar esto en SessionManager

    suspend fun getMessages(otherId: Int): Result<List<ChatMessage>> {
        return try {
            val response = api.getChatHistory(otherId)
            if (response.isSuccessful && response.body() != null) {
                val currentUserId = myMemberId
                val messages = response.body()!!.map { dto ->
                    ChatMessage(
                        id = dto.id,
                        content = dto.contenido,
                        isMine = dto.idRemitente == currentUserId,
                        timestamp = dto.fechaEnvio,
                        imageUrl = dto.urlImagen?.let { "$baseUrlStatic$it" },
                        isRead = dto.leido
                    )
                }
                Result.success(messages)
            } else {
                Result.failure(Exception("Error ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Enviar mensaje (Solución al 403: Enviamos idHogar)
    suspend fun sendMessage(recipientId: Int, content: String, hogarId: Int, imageUrl: String? = null): Result<Boolean> {
        return try {
            val request = MessageCreateRequestDto(
                idHogar = hogarId, // ¡CRÍTICO PARA EL ERROR 403!
                contenido = content,
                urlImagen = imageUrl
            )
            val response = api.sendMessage(recipientId, request)
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Error enviando: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun getMemberDetails(memberId: Int): Result<MemberUiModel> {
        return try {
            val response = api.getMemberDetails(memberId)
            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                val model = MemberUiModel(
                    id = dto.id,
                    name = dto.nombreCompleto,
                    email = dto.correoElectronico,
                    roleName = dto.rol?.nombre ?: "Miembro",
                    imageUrl = dto.imagenPerfil?.let { "$baseUrlStatic$it" }
                )
                Result.success(model)
            } else {
                Result.failure(Exception("Error cargando perfil"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadImage(file: File): Result<String> {
        return try {
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("archivo", file.name, requestFile)

            val response = api.uploadMessageImage(body)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.path) // Devolvemos la ruta relativa
            } else {
                Result.failure(Exception("Error subiendo imagen"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}