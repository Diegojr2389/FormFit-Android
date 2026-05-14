package com.example.formfit.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.formfit.models.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object UserDataStore {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

    val USER_ID = intPreferencesKey("user_id")
    val USERNAME =  stringPreferencesKey("username")
    val EMAIL = stringPreferencesKey("email")
    val PROFILE_PICTURE_URL = stringPreferencesKey("profile_picture_url")

    suspend fun saveUser(context: Context, userId: Int, username: String, email: String, profilePictureURL: String?) {
        context.dataStore.edit { prefs ->
            prefs[USER_ID] = userId
            prefs[USERNAME] = username
            prefs[EMAIL] = email
            profilePictureURL?.let { prefs[PROFILE_PICTURE_URL] = it }
        }
    }


    suspend fun updateProfilePictureURL(context: Context, profilePictureUrl: String) {
        context.dataStore.edit { prefs ->
            prefs[PROFILE_PICTURE_URL] = profilePictureUrl
        }
    }

    suspend fun updateUserInfo(context: Context, username: String, email: String) {
        context.dataStore.edit { prefs ->
            prefs[USERNAME] = username
            prefs[EMAIL] = email
        }
    }

    fun getUser(context: Context): Flow<UserData> {
        return context.dataStore.data.map { prefs ->
            UserData(
                prefs[USER_ID],
                prefs[USERNAME],
                prefs[PROFILE_PICTURE_URL],
                prefs[EMAIL]
            )
        }
    }

    suspend fun clearUser(context: Context) {
        context.dataStore.edit { it.clear() }
    }
}