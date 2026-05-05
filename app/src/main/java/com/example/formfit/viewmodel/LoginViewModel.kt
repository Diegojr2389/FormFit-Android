package com.example.formfit.viewmodel

import android.content.Context
import android.service.autofill.UserData
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewModelScope
import com.example.formfit.datastore.UserDataStore
import com.example.formfit.repository.AuthRepository
import com.example.formfit.network.RetrofitClient
import kotlinx.coroutines.launch

// ViewModel - sits between the UI and data layer
// A ViewModel holds your screen's data and logic separately from the UI, so it survives things
// like screen rotations without losing state.
class LoginViewModel : ViewModel() {
    private val repository = AuthRepository(RetrofitClient.apiService)

    var errorMessage by mutableStateOf<String?>(null)
        private set // only this view model can change the variable

    var isLoading by mutableStateOf(false)
        private set // only this view model can change the variable

    fun login(username: String, password: String, context: Context, onSuccess: (String) -> Unit) {
        // starts a coroutine (async work) that's tied to the ViewModel's lifecycle, so it cancels
        // automatically if the screen is destroyed
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = repository.loginUser(username, password)
                Log.d("login", "$response")
                UserDataStore.saveUser(context, response.userId, username)
                onSuccess(response.accessToken)
            } catch (e: Exception) {
                errorMessage = e.message ?: "Unknown error"
            } finally {
                isLoading = false
            }
        }
    }
}