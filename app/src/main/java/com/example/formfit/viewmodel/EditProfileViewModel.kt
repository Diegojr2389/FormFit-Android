package com.example.formfit.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.formfit.datastore.UserDataStore
import com.example.formfit.models.UpdateUserRequest
import com.example.formfit.network.ApiService
import com.example.formfit.network.RetrofitClient
import com.example.formfit.repository.EditProfileRepository
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class EditProfileViewModel(
    private val appContext : Context,
    private val apiService : ApiService
) : ViewModel() {
    var updateProfileErrorMessage by mutableStateOf<String?>(null)
        private set
    var updateProfileSuccessMessage by mutableStateOf<String?>(null)
        private set

    var updateProfilePictureErrorMessage by mutableStateOf<String?>(null)
        private set


    val repository = EditProfileRepository(
        RetrofitClient.create(appContext)
    )
    fun uploadProfilePicture(uri : Uri) {
        viewModelScope.launch {
            updateProfilePictureErrorMessage = null
            try {
                val stream = appContext.contentResolver.openInputStream(uri)!!
                val bytes = stream.readBytes()
                val requestBody = bytes.toRequestBody("image/*".toMediaType())
                val multiPart = MultipartBody.Part.createFormData("file", "profile.jpg", requestBody)

                repository.uploadProfilePicture(multiPart, appContext)
            } catch (e: Exception) {
                updateProfilePictureErrorMessage = e.message ?: "Unknown Error"
                Log.d("ProfilePicture", e.message ?: "Unknown error")
            }
        }
    }

    fun updateProfile(username: String, email: String, currentPassword: String?, newPassword: String?) {
        viewModelScope.launch {
            updateProfileErrorMessage = null
            try {
                val message = repository.updateProfile(UpdateUserRequest(username, email, currentPassword, newPassword))
                UserDataStore.updateUserInfo(appContext, username, email)
                updateProfileSuccessMessage = message
            } catch (e: Exception) {
                updateProfileErrorMessage = e.message ?: "Unknown error"
                Log.d("EditProfile", e.message ?: "Unknown error")
            }
        }
    }

    companion object {
        fun Factory(context : Context) : ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass : Class<T>) : T {
                    return EditProfileViewModel(
                        context.applicationContext,
                        RetrofitClient.create(context)
                    ) as T
                }
            }
    }
}