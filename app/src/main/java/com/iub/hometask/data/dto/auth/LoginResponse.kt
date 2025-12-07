package com.iub.hometask.data.dto.auth

// LoginResponseDto.kt (Lo que devuelve el backend, usualmente un Token)
data class LoginResponseDto(
    val access_token: String,
    val token_type: String
)