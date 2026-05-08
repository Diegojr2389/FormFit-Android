package com.example.formfit.MLKitPoseDetection

import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetector

@ExperimentalGetImage
class ImageAnalyzer(
    private val poseDetector: PoseDetector = PoseDetectorHelper().create(),
    private val onPoseDetected: (Pose, Int, Int, Int) -> Unit
) : ImageAnalysis.Analyzer {
    // called automatically by CameraX for every camera frame
    override fun analyze(imageProxy: ImageProxy) {
        // raw camera image
        val mediaImage = imageProxy.image ?: return

        // Gets the rotation of the camera frame in degrees (0, 90, 180, 270)
        val rotation = imageProxy.imageInfo.rotationDegrees

        // converts the raw camera frame into a format that MLKit's pose detector can process
        // rotation is applied so that the image is correctly oriented
        val inputImage = InputImage.fromMediaImage(mediaImage, rotation)

        // sends frame to the ML Kit pose detector for processing
        poseDetector.process(inputImage)
            .addOnSuccessListener { pose ->
                onPoseDetected(pose, mediaImage.width, mediaImage.height, rotation)
            }
            .addOnFailureListener { e ->
                Log.e("PoseDebug", "Pose detection failed", e)
            }
            .addOnCompleteListener { // after a success or failure
                // releases the frame so that CameraX can can send the next one
                // without this, the camera will freeze
                imageProxy.close()
            }
    }
}