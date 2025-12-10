package com.iub.hometask.data.remote.api

import com.iub.hometask.data.dto.chat.MessageCreateRequestDto
import com.iub.hometask.data.dto.chat.MessageResponseDto
import com.iub.hometask.data.dto.member.MemberResponseDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ChatApiService {

    // Obtener historial con un usuario
    @GET("mensajes/directo/{otro_id}")
    suspend fun getChatHistory(@Path("otro_id") otherId: Int): Response<List<MessageResponseDto>>

    // Enviar mensaje a un usuario
    @POST("mensajes/directo/{destinatario_id}")
    suspend fun sendMessage(
        @Path("destinatario_id") recipientId: Int,
        @Body request: MessageCreateRequestDto
    ): Response<MessageResponseDto> // O lo que devuelva tu backend (a veces devuelve el msg creado)

    @GET("miembros/{id}")
    suspend fun getMemberDetails(@Path("id") id: Int): Response<MemberResponseDto>

    // Subir imagen (Multipart)
    @Multipart
    @POST("uploads/mensajes")
    suspend fun uploadMessageImage(
        @Part file: MultipartBody.Part
    ): Response<UploadResponseDto>
}
data class UploadResponseDto(
    val path: String, // Ruta relativa
    val url: String   // URL absoluta o relativa
)