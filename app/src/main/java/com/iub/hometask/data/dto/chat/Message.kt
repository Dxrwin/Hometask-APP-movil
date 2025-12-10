package com.iub.hometask.data.dto.chat

import com.google.gson.annotations.SerializedName

// Respuesta del GET /mensajes/directo/{id}
data class MessageResponseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("contenido") val contenido: String,
    @SerializedName("id_remitente") val idRemitente: Int,
    @SerializedName("id_destinatario") val idDestinatario: Int,
    @SerializedName("fecha_envio") val fechaEnvio: String, // "2025-12-09T..."
    @SerializedName("url_imagen") val urlImagen: String?, // Ruta relativa "mensajes/abc.png"
    @SerializedName("leido") val leido: Boolean
)

// Body para POST /mensajes/directo/{id}
data class MessageCreateRequestDto(
    @SerializedName("id_hogar") val idHogar: Int,
    @SerializedName("contenido") val contenido: String,
    @SerializedName("url_imagen") val urlImagen: String? = null
)