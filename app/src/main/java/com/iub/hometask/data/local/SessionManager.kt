package com.iub.hometask.data.local

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("hometask_session", Context.MODE_PRIVATE)

    companion object {
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_USER_ROLE_ID = "user_role_id"
        const val KEY_USER_ID = "user_id"        // <--- NUEVO
        const val KEY_HOGAR_ID = "hogar_id"      // <--- NUEVO
        const val KEY_TOKEN_TIMESTAMP = "token_timestamp"
    }

    /**
     * Guarda toda la información de la sesión en un solo paso.
     */
    fun saveSession(token: String, roleId: Int, userId: Int, id_hogar: Int) {
        val editor = prefs.edit()
        editor.putString(KEY_ACCESS_TOKEN, token)
        editor.putInt(KEY_USER_ROLE_ID, roleId)
        editor.putInt(KEY_USER_ID, userId)       // <--- NUEVO
        editor.putInt(KEY_HOGAR_ID, id_hogar)     // <--- NUEVO
        editor.putLong(KEY_TOKEN_TIMESTAMP, System.currentTimeMillis())
        editor.apply()
    }

    fun fetchAuthToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }

    fun getUserRole(): Int {
        return prefs.getInt(KEY_USER_ROLE_ID, -1)
    }

    // Método para obtener el ID del usuario actual (para saber si el mensaje es mío)
    fun getUserId(): Int {
        return prefs.getInt(KEY_USER_ID, -1)
    }

    // Método para obtener el Hogar (necesario para enviar mensajes)
    fun getHogarId(): Int {
        return prefs.getInt(KEY_HOGAR_ID, -1)
    }

    fun clearSession() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }

    fun isTokenValid(): Boolean {
        val timestamp = prefs.getLong(KEY_TOKEN_TIMESTAMP, 0)
        val currentTime = System.currentTimeMillis()
        // 30 minutos de validez
        val thirtyMinutesInMillis = 30 * 60 * 1000
        return (currentTime - timestamp) < thirtyMinutesInMillis
    }
}