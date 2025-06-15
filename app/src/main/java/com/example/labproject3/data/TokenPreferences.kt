package com.example.labproject3.data

import android.content.Context
import android.content.SharedPreferences
import okhttp3.Interceptor
import okhttp3.Response

class TokenPreferences(context: Context) {
    private val preference: SharedPreferences =
        context.getSharedPreferences("", Context.MODE_PRIVATE)

    fun setToken(token: String) {
        val prefEditor: SharedPreferences.Editor = preference.edit()
        prefEditor.putString(TOKEN, token)
        prefEditor.apply()
    }

    fun getToken(): String {
        return preference.getString(TOKEN, "").toString()
    }

    fun removeToken() {
        val prefEditor: SharedPreferences.Editor = preference.edit()
        prefEditor.clear()
        prefEditor.apply()
    }

    companion object {
        const val TOKEN = "token"
    }
}