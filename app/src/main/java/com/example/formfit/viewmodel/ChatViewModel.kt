package com.example.formfit.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.formfit.models.ChatMessage
import com.example.formfit.network.RetrofitClient
import com.example.formfit.repository.ChatRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class ChatViewModel(
    context : Context
) : ViewModel() {
    val outputFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())

    val chatScreenOutputFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())

    val inputFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
    private val repository = ChatRepository(RetrofitClient.create(context))

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var messages by mutableStateOf<List<ChatMessage>>(emptyList())
        private set

    fun send(userId: Int, message: String, createdAt: String) {
        viewModelScope.launch {
            errorMessage = null
            isLoading = true

            val userDate = inputFormatter.parse(createdAt)
            messages = messages + listOf(
                ChatMessage(message = message, createdAt = outputFormatter.format(userDate!!), isUser = true, isNew = true)
            )

            try {
                val response = repository.sendMessage(userId, message, createdAt)
                val botDate = inputFormatter.parse(response.createdAt)
                messages = messages + listOf(
                    ChatMessage(message = response.response, createdAt = outputFormatter.format(botDate!!), isUser = false, isNew = true)
                )

            } catch(e: Exception) {
                errorMessage = e.message ?: "Unknown Error"
                Log.d("Chat", "Error: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    companion object {
        fun Factory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ChatViewModel(context) as T
                }
            }
    }
}