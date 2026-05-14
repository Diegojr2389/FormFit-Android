package com.example.formfit.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.formfit.datastore.SessionManager
import com.example.formfit.datastore.UserDataStore
import com.example.formfit.models.UserIssue
import com.example.formfit.network.RetrofitClient
import com.example.formfit.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val sessionManager : SessionManager,
    private val appContext: Context
) : ViewModel() {
    // MutableStateFlow - UI is notified instantly when this changes
    private val _loggedOut = MutableStateFlow(false)

    private val repository = ProfileRepository(RetrofitClient.create(appContext))

    // Public and read-only - UI can only observe it, not change it
    val loggedOut = _loggedOut.asStateFlow()

    var issues by mutableStateOf<List<UserIssue>>(emptyList())
    fun logout() {
        viewModelScope.launch { // runs following code in coroutine since clearSession() is a suspend function
            sessionManager.clearSession()
            UserDataStore.clearUser(appContext)
            _loggedOut.value = true
        }
    }

    fun getIssues() {
        viewModelScope.launch {
            try {
                val response = repository.getUserIssues()
                issues = response
            } catch (e : Exception) {
                Log.d("UserIssues", "Error: ${e.message}")
            }
        }
    }

    companion object { // Kotlin's version of a static method
        // Android can't create a ViewModel with constructor arguments on its own, so the Factory tells it how
        fun Factory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ProfileViewModel(
                        SessionManager(context.applicationContext),
                        context.applicationContext) as T
                }
            }
    }
}