package com.example.formfit.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

class SessionManager(private val context: Context) {
    companion object  {
        private val Context.datastore by preferencesDataStore("session")

        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }

    suspend fun saveTokens(access: String, refresh: String) {
        context.datastore.edit {
            it[ACCESS_TOKEN] = access
            it[REFRESH_TOKEN] = refresh
        }
    }

    suspend fun getAccessToken(): String? {
        return context.datastore.data.first()[ACCESS_TOKEN]
    }

    suspend fun getRefreshToken(): String? {
        return context.datastore.data.first()[REFRESH_TOKEN]
    }

    suspend fun isLoggedIn(): Boolean {
        return getAccessToken() != null &&
                getRefreshToken() != null
    }

    suspend fun clearSession() {
        context.datastore.edit {
            it.clear()
        }
    }
}