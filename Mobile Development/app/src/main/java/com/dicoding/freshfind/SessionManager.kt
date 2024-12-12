package com.dicoding.freshfind.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

object SessionManager {
    private const val PREF_NAME = "UserSession"
    private const val IS_LOGGED_IN = "isLoggedIn"
    private const val USER_ROLE = "role"
    private const val KEY_TOKEN = "accessToken"

    fun saveLoginStatus(context: Context, isLoggedIn: Boolean) {
        val preferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putBoolean(IS_LOGGED_IN, isLoggedIn)
        editor.apply()
    }

    fun saveUserRole(context: Context, role: String) {
        val preferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString(USER_ROLE, role)
        editor.apply()
    }

    fun getRole(context: Context): String? {
        val preferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return preferences.getString(USER_ROLE, null)
    }

    fun isLoggedIn(context: Context): Boolean {
        val preferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return preferences.getBoolean(IS_LOGGED_IN, false)
    }

    fun clearSession(context: Context) {
        val preferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.clear()
        editor.apply()
    }

    fun saveToken(context: Context, token: String) {
        val preferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        preferences.edit().putString(KEY_TOKEN, token).apply()
        Log.d("SessionManager", "Token saved: $token")
    }

    fun getToken(context: Context): String? {
        val preferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val token = preferences.getString(KEY_TOKEN, null)
        Log.d("SessionManager", "Retrieved token: $token")
        return token
    }

    fun clearToken(context: Context) {
        val preferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        preferences.edit().remove(KEY_TOKEN).apply()
    }
}
