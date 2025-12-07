package com.iub.hometask.data.remote.api

import com.iub.hometask.data.dto.auth.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    //[cite_start]// Backend: @router.post("/login")
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<TokenResponseDto>

    //[cite_start]// Backend: @router.post("/registro")
    // Nota: El backend devuelve un Token al registrarse, igual que el login.
    @POST("auth/registro")
    suspend fun registrar(@Body request: RegisterRequestDto): Response<TokenResponseDto>
}