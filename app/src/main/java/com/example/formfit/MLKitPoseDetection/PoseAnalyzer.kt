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

    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: return

        val rotation = imageProxy.imageInfo.rotationDegrees

        // rotates the image
        val inputImage = InputImage.fromMediaImage(mediaImage, rotation)

        // process current camera frame
        poseDetector.process(inputImage)
            .addOnSuccessListener { pose ->
                onPoseDetected(pose, mediaImage.width, mediaImage.height, rotation)
            }
            .addOnFailureListener { e ->
                Log.e("PoseDebug", "Pose detection failed", e)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
}