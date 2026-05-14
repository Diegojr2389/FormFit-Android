package com.example.formfit.repository

import android.content.Context
import com.example.formfit.datastore.UserDataStore
import com.example.formfit.models.UpdateUserRequest
import com.example.formfit.network.ApiService
import okhttp3.MultipartBody
import org.json.JSONObject

class EditProfileRepository(
    private val api: ApiService
) {
    suspend fun uploadProfilePicture(multipartBodyPart: MultipartBody.Part, appContext : Context) {
        val response = api.uploadProfilePicture(multipartBodyPart)

        if (response.isSuccessful) {
            val url = response.body()!!.profilePictureUrl
            UserDataStore.updateProfilePictureURL(appContext, url)
        }
        else {
            val errorBody = response.errorBody()?.string() ?: "" // raw JSON string
            val errorMessage = JSONObject(errorBody).getString("detail")
            throw Exception(errorMessage)
        }
    }

    suspend fun updateProfile(updateUserRequest: UpdateUserRequest): String? {
        val response = api.updateProfile(updateUserRequest)

        if (response.isSuccessful) {
            return response.body()?.detail
        }
        else {
            val errorBody = response.errorBody()?.string() ?: "" // raw JSON string
            val errorMessage = JSONObject(errorBody).getString("detail")
            throw Exception(errorMessage)
        }
    }
}