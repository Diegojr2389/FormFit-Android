package com.example.formfit.repository

import com.example.formfit.models.ChatRequest
import com.example.formfit.models.ChatResponse
import com.example.formfit.network.ApiService

class ChatRepository(
    private val api: ApiService
) {
    suspend fun sendMessage(userId: Int, message: String, createdAt: String): ChatResponse {
        val response = api.sendMessage(ChatRequest(userId, message, createdAt))

        if (response.isSuccessful && response.body() != null) {
            return response.body()!!
        } else {
            throw Exception("Failed to send message")
        }
    }
}