package com.example.formfit.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object UserDataStore {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

    val USER_ID = intPreferencesKey("user_id")
    val USERNAME =  stringPreferencesKey("username")

    suspend fun saveUser(context: Context, userId: Int, username: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_ID] = userId
            prefs[USERNAME] = username
        }
    }

    fun getUser(context: Context): Flow<Pair<Int?, String?>> {
        return context.dataStore.data.map { prefs ->
            Pair(prefs[USER_ID], prefs[USERNAME])
        }
    }

    suspend fun clearUser(context: Context) {
        context.dataStore.edit { it.clear() }
    }
}