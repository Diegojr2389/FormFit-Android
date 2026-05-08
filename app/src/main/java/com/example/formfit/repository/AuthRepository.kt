package com.example.formfit.repository

import android.util.Log
import com.example.formfit.datastore.SessionManager
import com.example.formfit.models.SignUpRequest
import com.example.formfit.models.TokenResponse
import com.example.formfit.network.ApiService
import org.json.JSONObject

class AuthRepository(
    private val api: ApiService,
    private val sessionManager: SessionManager
) {
    suspend fun loginUser(username: String, password: String): TokenResponse {
        val response = api.login(username, password)

        if (response.isSuccessful && response.body() != null) {
            Log.d("login", "${response.body()}")

            val tokenResponse  =  TokenResponse(
                accessToken = response.body()!!.accessToken,
                refreshToken = response.body()!!.refreshToken,
                tokenType = response.body()!!.tokenType,
                userId = response.body()!!.userId
            )

            sessionManager.saveTokens(
                tokenResponse.accessToken,
                tokenResponse.refreshToken
            )

            Log.d("login", "$tokenResponse")

            return tokenResponse
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