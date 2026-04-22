package com.example.formfit.data.local

import com.example.formfit.models.Exercise

val EXERCISES = listOf(
    Exercise(
        id = "squat",
        name = "Squat",
        primaryMuscle = listOf("Quadriceps, Glutes"),
        secondaryMuscle = listOf("Abs", "Erector Spinae", "Hamstrings", "Calves"),
        description = "A foundational lower-body exercise performed by hinging at the hips and bending the knees to lower the body until thighs are parallel to the floor, then driving through the heels to return to standing. Builds overall leg strength and size."
    ),
    Exercise(
        id = "pushup",
        name = "Pushup",
        primaryMuscle = listOf("Pectoralis Major", "Triceps Brachii", "Anterior Deltoids"),
        secondaryMuscle = listOf("Pectoralis Minor", "Rear Deltoids"),
        description = "A bodyweight pressing movement performed face-down on the floor, lowering the chest to the ground by bending the elbows then pushing back up. Builds upper body pushing strength with no equipment needed."
    ),
    Exercise(
        id = "hanging-leg-raise",
        name = "Hanging Leg Raise",
        primaryMuscle = listOf("Rectus Abdominis"),
        secondaryMuscle = listOf("Hip Flexors", "Obliques"),
        description = "Performed by hanging from a bar and raising the legs upward by flexing at the hips, then lowering them with control. One of the most effective exercises for building lower abdominal strength."
    ),
    Exercise(
        id = "conventional-barbell-deadlift",
        name = "Conventional Barbell Deadlift",
        primaryMuscle = listOf("Glutes", "Hamstrings"),
        secondaryMuscle = listOf("Quads", "Spinal Erectors", "Lats", "Traps"),
        description = "A compound pulling movement where a loaded barbell is lifted from the floor to hip height by extending the hips and knees simultaneously. One of the best overall strength and mass builders for the entire posterior chain."
    ),
    Exercise(
        id = "hip-thrust",
        name = "Hip Thrust",
        primaryMuscle = listOf("Glutes", "Hamstrings"),
        secondaryMuscle = listOf("Erector Spinae", "Rectus Abdominis"),
        description = "Performed by resting the upper back on a bench with a barbell across the hips, then driving the hips upward by squeezing the glutes, and lowering back down with control. Highly effective for isolating and building glute strength and size."
    ),
    Exercise(
        id = "barbell-shoulder-overhead-press",
        name = "Barbell Overhead Shoulder Press",
        primaryMuscle = listOf("Anterior Deltoids", "Upper Pec"),
        secondaryMuscle = listOf("Triceps Brachii", "Upper Trapezius"),
        description = "A standing or seated press where a barbell is pushed from shoulder height directly overhead until the arms are fully extended, then lowered back down. Builds shoulder size and overhead pressing strength."
    ),
    Exercise(
        id = "dip",
        name = "Dip",
        primaryMuscle = listOf("Upper Pecs", "Lower Pecs"),
        secondaryMuscle = listOf("Anterior Deltoids", "Triceps Brachii"),
        description = "A bodyweight movement performed on parallel bars by lowering the body by bending the elbows, then pressing back up to full extension. Chest emphasis increases by leaning forward, while staying upright shifts focus to the triceps."
    ),
    Exercise(
        id = "barbell-curl",
        name = "Barbell Curl",
        primaryMuscle = listOf("Brachialis", "Biceps Brachii", "Brachioradialis"),
        secondaryMuscle = listOf("Supinator", "Anterior Deltoid", "Anterior Forearm"),
        description = "A standing curl where a barbell is lifted from hip height to shoulder height by flexing the elbows, then lowered with control. A staple movement for building overall bicep mass and arm thickness."
    ),
//    Exercise(
//        id = "bench-press",
//        name = "Bench Press",
//        primaryMuscle = listOf("Pectoralis Major"),
//        secondaryMuscle = listOf("Triceps Brachii", "Anterior Deltoids", "Latissimus Dorsi"),
//        description = ""
//    ),
//    Exercise(
//        id = "skull-crusher",
//        name = "Skull Crusher",
//        primaryMuscle = listOf("Triceps Brachii"),
//        secondaryMuscle = listOf("Anterior Deltoids", "Chest"),
//        description = ""
//    ),
    Exercise(
        id = "lateral-raise",
        name = "Lateral Raise",
        primaryMuscle = listOf("Lateral Deltoids"),
        secondaryMuscle = listOf("Anterior Deltoids"),
        description = "Performed by raising dumbbells out to the sides until arms are parallel to the floor, then lowering with control. The primary isolation exercise for the lateral deltoids, responsible for shoulder width."
    ),
    Exercise(
        id = "plank",
        name = "Plank",
        primaryMuscle = listOf("Rectus Abdominis", "Transverse Abdominis"),
        secondaryMuscle = listOf("Obliques"),
        description = "A static hold performed face-down with bodyweight supported on the forearms and toes, maintaining a straight line from head to heels. Builds core stability and endurance throughout the entire anterior chain."
    ),
    Exercise(
        id = "romanian-deadlift",
        name = "Romanian Deadlift",
        primaryMuscle = listOf("Glutes", "Hamstrings"),
        secondaryMuscle = listOf("Spinal Erectors"),
        description = "A hip hinge movement where a barbell is lowered toward the floor by pushing the hips back with soft knees, then returned to standing by driving the hips forward. Emphasizes the hamstrings and glutes through a long range of motion."
    ),
    Exercise(
        id = "leg-extension",
        name = "Leg Extension",
        primaryMuscle = listOf("Quads"),
        secondaryMuscle = listOf("Hip Flexors", "Hamstrings"),
        description = "A machine-based isolation exercise where the legs are extended from a bent position to fully straight against a weighted pad, then lowered back down. Directly targets all four heads of the quadriceps."
    ),
    Exercise(
        id = "dumbbell-row",
        name = "Dumbbell Row",
        primaryMuscle = listOf("Latissimus Dorsi"),
        secondaryMuscle = listOf("Mid Trapezius"),
        description = "Performed with one knee and hand braced on a bench, pulling a dumbbell from a hanging position up toward the hip by driving the elbow back. Builds unilateral back thickness and corrects side-to-side imbalances."
    ),
    Exercise(
        id = "pullup",
        name = "Pullup",
        primaryMuscle = listOf("Lats", "Rhomboids", "Teres Major"),
        secondaryMuscle = listOf("Brachialis", "Biceps", "Brachioradialis", "Lower Trapezius", "Rectus Abdominis"),
        description = "A bodyweight pulling movement where the body is lifted from a dead hang until the chin clears the bar, then lowered with control. One of the best exercises for building lat width and overall upper body pulling strength."
    ),
    Exercise(
        id = "upright-row",
        name = "Upright Row",
        primaryMuscle = listOf("Lateral Deltoids", "Upper Trapezius"),
        secondaryMuscle = listOf("Biceps"),
        description = "Performed by pulling a barbell or dumbbells vertically from hip height to chin height with elbows flaring out, then lowering with control. Effectively targets the lateral deltoids and upper traps for shoulder and neck development."
    ),
    Exercise(
        id = "good-morning",
        name = "Good Morning",
        primaryMuscle = listOf("Glutes", "Hamstrings"),
        secondaryMuscle = listOf("Spinal Erectors", "Upper Back"),
        description = "A barbell movement where the bar is placed across the upper back and the torso hinges forward at the hips with soft knees until nearly parallel to the floor, then returns to upright. Strengthens the entire posterior chain with emphasis on the hamstrings and spinal erectors."
    ),
    Exercise(
        id = "preacher-curl",
        name = "Preacher Curl",
        primaryMuscle = listOf("Biceps Brachii"),
        secondaryMuscle = listOf("Brachialis", "Brachioradialis"),
        description = "Performed on a preacher bench with the upper arms braced against the pad, curling a barbell or dumbbells from full extension to full flexion. The braced position eliminates momentum and places maximum tension on the biceps throughout the range of motion."
    ),
)