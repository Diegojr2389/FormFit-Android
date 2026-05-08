package com.example.formfit.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.formfit.datastore.SessionManager
import com.example.formfit.repository.AuthRepository
import com.example.formfit.network.RetrofitClient
import kotlinx.coroutines.launch

class SignUpViewModel: ViewModel() {
    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun signup(username: String, email: String, password: String, context: Context, onSuccess: () -> Unit) {
        val sessionManager = SessionManager(context)

        val repository = AuthRepository(
            RetrofitClient.create(context),
            sessionManager
        )

        viewModelScope.launch {
            errorMessage = null
            isLoading = true

            try {
                repository.signUpUser(username, email, password)
                onSuccess()
            } catch(e: Exception) {
                errorMessage = e.message ?: "Unknown Error"
            } finally {
                isLoading = false
            }
        }
    }
}