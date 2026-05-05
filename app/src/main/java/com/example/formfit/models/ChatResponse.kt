package com.example.formfit.models

import com.google.gson.annotations.SerializedName

data class ChatResponse(
    val response: String,
    @SerializedName("created_at")
    val createdAt: String
)