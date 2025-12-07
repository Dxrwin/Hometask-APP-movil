package com.iub.hometask.data.remote

import com.iub.hometask.data.remote.api.AuthApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // ⚠IMPORTANTE: 10.0.2.2 es el "localhost" del emulador de Android Studio.
    // No uses 127.0.0.1 ni localhost.
    private const val BASE_URL = "http://192.168.1.43:8000/"

    val authService: AuthApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }
}