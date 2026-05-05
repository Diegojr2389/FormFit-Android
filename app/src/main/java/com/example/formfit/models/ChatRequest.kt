package com.example.formfit.models

import com.google.gson.annotations.SerializedName

data class ChatRequest (
    @SerializedName("user_id")
    val userId: Int,
    val message: String,
    @SerializedName("created_at")
    val createdAt: String
)