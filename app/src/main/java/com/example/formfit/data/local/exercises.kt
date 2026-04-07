package com.example.formfit.data.local

import com.example.formfit.models.Exercise

val EXERCISES = listOf(
    Exercise(
        id = "squat",
        name = "Squat",
        primaryMuscle = listOf("Quadriceps (Quads)"),
        secondaryMuscle = listOf("Abs", "Lower back", "Hamstrings", "Glutes", "Calves"),
        description = "The squat is a fundamental lower-body exercise that strengthens the quadriceps, glutes, and hamstrings. It involves bending the knees and hips to lower the body while keeping the back straight, then returning to a standing position. Proper form is essential to maximize strength gains and prevent injury."
    ),
    Exercise(
        id = "pushup",
        name = "Pushup",
        primaryMuscle = listOf(""),
        secondaryMuscle = emptyList(),
        description = ""
    ),
    Exercise(
        id = "conventional-deadlift",
        name = "Conventional Deadlift",
        primaryMuscle = listOf("Glutes", "Hamstrings"),
        secondaryMuscle = listOf("Quads", "Spinal Erectors", "Lats", "Traps"),
        description = ""
    ),
    Exercise(
        id = "pullup",
        name = "Pullup",
        primaryMuscle = listOf(""),
        secondaryMuscle = emptyList(),
        description = ""
    ),
    Exercise(
        id = "shoulder-press",
        name = "Shoulder Press",
        primaryMuscle = listOf(""),
        secondaryMuscle = emptyList(),
        description = ""
    ),
    Exercise(
        id = "barbell-curl",
        name = "Barbell Curl",
        primaryMuscle = listOf(""),
        secondaryMuscle = emptyList(),
        description = ""
    ),
    Exercise(
        id = "leg-press",
        name = "Leg Press",
        primaryMuscle = listOf(""),
        secondaryMuscle = emptyList(),
        description = ""
    ),
    Exercise(
        id = "standing-calf-raise",
        name = "Standing Calf Raise",
        primaryMuscle = listOf(""),
        secondaryMuscle = emptyList(),
        description = ""
    ),
    Exercise(
        id = "plank",
        name = "Plank",
        primaryMuscle = listOf(""),
        secondaryMuscle = emptyList(),
        description = ""
    ),
    Exercise(
        id = "hanging-leg-raise",
        name = "Hanging Leg Raise",
        primaryMuscle = listOf(""),
        secondaryMuscle = emptyList(),
        description = ""
    ),
    Exercise(
        id = "bench-press",
        name = "Bench Press",
        primaryMuscle = listOf("Pectoralis Major"),
        secondaryMuscle = listOf("Triceps Brachii", "Anterior Deltoid", "Latissimus Dorsi"),
        description = ""
    ),
    Exercise(
        id = "dumbbell-fly",
        name = "Dumbbell Fly",
        primaryMuscle = listOf(""),
        secondaryMuscle = emptyList(),
        description = ""
    )
)