package com.renata.data.user.login

import android.content.Context

class LoginPreferences(context: Context) {

    private val preferences = context.getSharedPreferences(PREFS_ID, Context.MODE_PRIVATE)

    fun setLogin(value: LoginResult) {
        val editor = preferences.edit()
        editor.putString(ID, value.id)
        editor.putString(EMAIL, value.email)
        editor.putString(TOKEN, value.token)
        editor.apply()
    }

    fun getString(key: String): String? {
        return preferences.getString(key, null)
    }

    fun getUser(): LoginResult {
        val id = preferences.getString(ID, null)
        val email = preferences.getString(EMAIL, null)
        val token = preferences.getString(TOKEN, null)
        return LoginResult(id, email, token)
    }

    fun removeUser() {
        val editor = preferences.edit().clear()
        editor.apply()
    }

    companion object {
        const val PREFS_ID = "login_pref"
        const val ID = "id"
        const val EMAIL = "email"
        const val TOKEN = "token"
    }
}