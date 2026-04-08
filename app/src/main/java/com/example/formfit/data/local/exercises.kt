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
        primaryMuscle = listOf("Pectoralis Major", "Triceps Brachii", "Anterior Deltoids"),
        secondaryMuscle = listOf("Pectoralis Minor", "Rear Deltoids"),
        description = ""
    ),
    Exercise(
        id = "conventional-barbell-deadlift",
        name = "Conventional Barbell Deadlift",
        primaryMuscle = listOf("Glutes", "Hamstrings"),
        secondaryMuscle = listOf("Quads", "Spinal Erectors", "Lats", "Traps"),
        description = ""
    ),
    Exercise(
        id = "pullup",
        name = "Pullup",
        primaryMuscle = listOf("Lats", "Rhomboids", "Teres Major"),
        secondaryMuscle = listOf("Brachialis", "Biceps", "Brachioradialis", "Lower Trapezius", "Rectus Abdominis"),
        description = ""
    ),
    Exercise(
        id = "barbell-shoulder-overhead-press",
        name = "Barbell Overhead Shoulder Press",
        primaryMuscle = listOf("Anterior Deltoids", "Upper Pec"),
        secondaryMuscle = listOf("Triceps Brachii", "Upper Trapezius"),
        description = ""
    ),
    Exercise(
        id = "barbell-curl",
        name = "Barbell Curl",
        primaryMuscle = listOf("Brachialis", "Biceps Brachii", "Brachioradialis"),
        secondaryMuscle = listOf("Supinator", "Anterior Deltoid", "Anterior Forearm"),
        description = ""
    ),
    Exercise(
        id = "bench-press",
        name = "Bench Press",
        primaryMuscle = listOf("Pectoralis Major"),
        secondaryMuscle = listOf("Triceps Brachii", "Anterior Deltoids", "Latissimus Dorsi"),
        description = ""
    ),
    Exercise(
        id = "skull-crusher",
        name = "Skull Crusher",
        primaryMuscle = listOf("Triceps Brachii"),
        secondaryMuscle = listOf("Anterior Deltoids", "Chest"),
        description = ""
    ),
    Exercise(
        id = "lateral-raise",
        name = "Lateral Raise",
        primaryMuscle = listOf("Lateral Deltoids"),
        secondaryMuscle = listOf("Anterior Deltoids"),
        description = ""
    ),
    Exercise(
        id = "romanian-deadlift",
        name = "Romanian Deadlift",
        primaryMuscle = listOf("Glutes", "Hamstrings"),
        secondaryMuscle = listOf("Spinal Erectors"),
        description = ""
    ),
    Exercise(
        id = "leg-extension",
        name = "Leg Extension",
        primaryMuscle = listOf("Quadriceps"),
        secondaryMuscle = listOf("Hip Flexors", "Hamstrings"),
        description = ""
    ),
    Exercise(
        id = "dumbbell-row",
        name = "Dumbbell Row",
        primaryMuscle = listOf("Latissimus Dorsi"),
        secondaryMuscle = listOf("Mid Trapezius"),
        description = ""
    ),
    Exercise(
        id = "hanging-leg-raise",
        name = "Hanging Leg Raise",
        primaryMuscle = listOf("Rectus Abdominis"),
        secondaryMuscle = listOf("Hip Flexors", "Obliques"),
        description = ""
    ),
    Exercise(
        id = "plank",
        name = "Plank",
        primaryMuscle = listOf("Rectus Abdominis", "Transverse Abdominis"),
        secondaryMuscle = listOf("Obliques"),
        description = ""
    ),
    Exercise(
        id = "hip-thrust",
        name = "Hip Thrust",
        primaryMuscle = listOf("Glutes", "Hamstrings"),
        secondaryMuscle = listOf("Erector Spinae", "Rectus Abdominis"),
        description = ""
    ),
    Exercise(
        id = "dip",
        name = "Dip",
        primaryMuscle = listOf("Upper Pecs", "Lower Pecs"),
        secondaryMuscle = listOf("Anterior Deltoids", "Triceps Brachii"),
        description = ""
    ),
    Exercise(
        id = "upright-row",
        name = "Upright Row",
        primaryMuscle = listOf("Lateral Deltoids", "Upper Trapezius"),
        secondaryMuscle = listOf("Biceps"),
        description = ""
    ),
    Exercise(
        id = "good-morning",
        name = "Good Morning",
        primaryMuscle = listOf("Glutes", "Hamstrings"),
        secondaryMuscle = listOf("Spinal Erectors", "Upper Back"),
        description = ""
    ),
    Exercise(
        id = "preacher-curl",
        name = "Preacher Curl",
        primaryMuscle = listOf("Biceps Brachii"),
        secondaryMuscle = listOf("Brachialis", "Brachioradialis"),
        description = ""
    ),
)