package com.example.campus

import android.content.Context

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)

    fun saveLogin(name: String, email: String, idToken: String) {
        prefs.edit()
            .putBoolean("logged_in", true)
            .putString("name", name)
            .putString("email", email)
            .putString("id_token", idToken)
            .apply()
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean("logged_in", false)
    fun name(): String = prefs.getString("name", "") ?: ""
    fun email(): String = prefs.getString("email", "") ?: ""
    fun idToken(): String = prefs.getString("id_token", "") ?: ""

    fun clear() = prefs.edit().clear().apply()
}