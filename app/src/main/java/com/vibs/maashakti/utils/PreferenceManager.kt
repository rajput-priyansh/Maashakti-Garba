package com.vibs.maashakti.utils

import android.content.Context
import android.content.SharedPreferences


object PreferenceManager {

    private const val PREFERENCES_FILE_NAME = "Maashakti App"
    var preferences: SharedPreferences? = null

    private const val USER_TOKEN = "user token"

    fun with(context: Context) {
        preferences = context.getSharedPreferences(
            PREFERENCES_FILE_NAME, Context.MODE_PRIVATE
        )
    }

    fun clearPrefs() {
        preferences?.edit()?.apply {
            clear()
            apply()
        }
    }

    fun saveUserToken(userToken: String?) {
        preferences?.edit()?.putString(USER_TOKEN, userToken)?.apply()
    }

    fun getUserToken(): String {
        return preferences?.getString(USER_TOKEN, "") ?: ""
    }
}
