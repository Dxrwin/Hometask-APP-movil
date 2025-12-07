package com.iub.hometask.data.local

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("hometask_session", Context.MODE_PRIVATE)

    companion object {
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_USER_ROLE_ID = "user_role_id"
        const val KEY_TOKEN_TIMESTAMP = "token_timestamp"
    }

    // Guardar sesión tras login exitoso
    fun saveAuthToken(token: String, roleId: Int) {
        val editor = prefs.edit()
        editor.putString(KEY_ACCESS_TOKEN, token)
        editor.putInt(KEY_USER_ROLE_ID, roleId)
        editor.putLong(KEY_TOKEN_TIMESTAMP, System.currentTimeMillis()) // Para controlar los 30 min
        editor.apply()
    }

    // Obtener Token para las peticiones
    fun fetchAuthToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }

    // Obtener Rol para saber qué pantalla mostrar
    fun getUserRole(): Int {
        return prefs.getInt(KEY_USER_ROLE_ID, -1) // -1 si no hay rol
    }

    // Cerrar sesión
    fun clearSession() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }

    // Validar si el token sigue "vivo" (opcional, lógica simple de 30 min)
    fun isTokenValid(): Boolean {
        val timestamp = prefs.getLong(KEY_TOKEN_TIMESTAMP, 0)
        val currentTime = System.currentTimeMillis()
        val thirtyMinutesInMillis = 30 * 60 * 1000
        return (currentTime - timestamp) < thirtyMinutesInMillis
    }
}