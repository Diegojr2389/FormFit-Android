package com.example.formfit.repository

import android.util.Log
import com.example.formfit.datastore.SessionManager
import com.example.formfit.models.LoginResponse
import com.example.formfit.models.SignUpRequest
import com.example.formfit.network.ApiService
import org.json.JSONObject

class AuthRepository(
    private val api: ApiService,
    private val sessionManager: SessionManager
) {
    suspend fun loginUser(username: String, password: String): LoginResponse {
        val response = api.login(username, password)

        if (response.isSuccessful && response.body() != null) {
            Log.d("Auth login", "${response.body()}")

            val loginResponse = LoginResponse(
                accessToken = response.body()!!.accessToken,
                refreshToken = response.body()!!.refreshToken,
                tokenType = response.body()!!.tokenType,
                userId = response.body()!!.userId,
                username = response.body()!!.username,
                email = response.body()!!.email,
                profilePictureUrl = response.body()!!.profilePictureUrl

            )

            sessionManager.saveTokens(
                loginResponse.accessToken,
                loginResponse.refreshToken
            )

            Log.d("login", "$loginResponse")

            return loginResponse
        } else {
            val errorBody = response.errorBody()?.string() ?: ""
            val errorMessage = JSONObject(errorBody).getString("detail")
            throw Exception(errorMessage)
        }
    }
    suspend fun signUpUser(username: String, email: String, password: String) {
        val response = api.signup(SignUpRequest(username, email, password))
        if (!response.isSuccessful) {
            val errorBody = response.errorBody()?.string() ?: "" // raw JSON string
            val errorMessage = JSONObject(errorBody).getString("detail")
            throw Exception(errorMessage)
        }
    }
}