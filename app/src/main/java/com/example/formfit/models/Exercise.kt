package com.example.formfit.models

// structured representation of an exercise
data class Exercise(
    val id: String,
    val name: String,
    val primaryMuscle: List<String>,
    val secondaryMuscle: List<String>,
    val description: String
)