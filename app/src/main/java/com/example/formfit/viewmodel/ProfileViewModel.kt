package com.example.formfit.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.formfit.datastore.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val sessionManager : SessionManager
) : ViewModel() {
    // MutableStateFlow - UI is notified instantly when this changes
    private val _loggedOut = MutableStateFlow(false)

    // Public and read-only - UI can only observe it, not change it
    val loggedOut = _loggedOut.asStateFlow()
    fun logout() {
        viewModelScope.launch { // runs following code in coroutine since clearSession() is a suspend function
            sessionManager.clearSession()
            _loggedOut.value = true
        }
    }

    companion object { // Kotlin's version of a static method
        // Android can't create a ViewModel with constructor arguments on its own, so the Factory tells it how
        fun Factory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ProfileViewModel(SessionManager(context)) as T
                }
            }
    }
}