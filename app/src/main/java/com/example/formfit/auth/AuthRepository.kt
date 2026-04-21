package com.example.formfit.auth

import android.util.Log
import com.example.formfit.network.ApiService

class AuthRepository(
    private val api: ApiService
) {
    suspend fun loginUser(username: String, password: String): String {
        val response = api.login(username, password)

        if (response.isSuccessful && response.body() != null) {
            return response.body()!!.access_token
        } else {
            throw Exception("Login Failed")
        }
    }
}