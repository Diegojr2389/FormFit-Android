package com.example.formfit.repository

import com.example.formfit.models.UserIssue
import com.example.formfit.network.ApiService

class ProfileRepository(
    private val api : ApiService
) {
    suspend fun getUserIssues() : List<UserIssue> {
        val response = api.getUserIssues()

        if (response.isSuccessful && response.body() != null) {
            return response.body()!!
        } else {
            throw Exception("Failed to get user issues")
        }
    }
}