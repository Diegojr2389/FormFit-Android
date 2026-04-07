package com.example.formfit.MLKitPoseDetection

import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions

class PoseDetectorHelper {
    // Creates and returns an ML Kit PoseDetector configured in STREAM_MODE
    // STREAM_MODE is used for real-time camera input
    fun create(): PoseDetector =
        PoseDetection.getClient(
            PoseDetectorOptions.Builder()
            .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
            .build()
        )
}
