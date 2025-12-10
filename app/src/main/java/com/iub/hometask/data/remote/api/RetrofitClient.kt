package com.iub.hometask.data.remote

import android.content.Context
import com.iub.hometask.data.local.SessionManager
import com.iub.hometask.data.remote.api.AuthApiService
import com.iub.hometask.data.remote.api.ChatApiService
import com.iub.hometask.data.remote.api.MemberApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // ⚠IMPORTANTE: 10.0.2.2 es el "localhost" del emulador de Android Studio.
    // No uses 127.0.0.1 ni localhost.
    private const val BASE_URL = "http://192.168.2.13:8000/"

    // Variable para guardar el cliente HTTP
    private var retrofit: Retrofit? = null

    fun getAuthService(context: Context): AuthApiService {
        if (retrofit == null) {
            val sessionManager = SessionManager(context)

            // 1. Interceptor de Auth (Inyecta el Token)
            val authInterceptor = Interceptor { chain ->
                val original = chain.request()
                val token = sessionManager.fetchAuthToken()

                val requestBuilder = original.newBuilder()
                // Siempre agregamos el header, si hay token
                if (!token.isNullOrEmpty()) {
                    requestBuilder.header("Authorization", "Bearer $token")
                }

                chain.proceed(requestBuilder.build())
            }

            // 2. NUEVO: Interceptor de Logs (El "Espía")
            val loggingInterceptor = HttpLoggingInterceptor { message ->
                // Este tag "API_LOG" te ayudará a filtrar en Logcat
                android.util.Log.d("API_LOG", message)
            }.apply {
                // BODY muestra todo: Headers, JSON enviado y recibido
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }


        return retrofit!!.create(AuthApiService::class.java)
    }

    fun getMemberService(context: Context): MemberApiService {
        // Aseguramos que Retrofit esté inicializado (llamando a la lógica base si es null)
        if (retrofit == null) {
            getAuthService(context) // Esto inicializa la variable 'retrofit'
        }
        return retrofit!!.create(MemberApiService::class.java)
    }

    // ... en RetrofitClient
    fun getChatService(context: Context): ChatApiService {
        if (retrofit == null) {
            getAuthService(context)
        }
        return retrofit!!.create(ChatApiService::class.java)
    }
}