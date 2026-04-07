package com.example.formfit.ui.components.feedback

import android.util.Log
import com.google.mlkit.vision.pose.Pose

fun provideConventionalDeadliftFeedback(pose: Pose? = null): String {
    Log.d("Feedback", "DOING A CONVENTIONAL DEADLIFT")
    return ""
}