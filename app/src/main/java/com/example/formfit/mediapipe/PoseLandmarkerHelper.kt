//package com.example.formfit.mediapipe
//
//import android.content.Context
//import android.util.Log
//import com.google.mediapipe.framework.image.MPImage
//import com.google.mediapipe.tasks.core.BaseOptions
//import com.google.mediapipe.tasks.vision.core.RunningMode
//import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
//
//class PoseLandmarkerHelper(private val context: Context) {
//    // MediaPipe PoseLandmarker Instance
//    private var poseLandmarker: PoseLandmarker? = null
//
//    fun setupPoseLandmarker() {
//        try {
//            val modelName = "pose_landmarker_full.task"
//
//            val baseOptions = BaseOptions.builder()
//                .setModelAssetPath(modelName)
//                .build()
//
//            val options = PoseLandmarker.PoseLandmarkerOptions.builder()
//                .setBaseOptions(baseOptions)
//                .setRunningMode(RunningMode.LIVE_STREAM)
//                .setResultListener { result, timestamp ->
//                    Log.d("PoseDebug", "Got pose result at $timestamp")
//                }
//                .build()
//
//            poseLandmarker = PoseLandmarker.createFromOptions(context, options)
//            Log.d("PoseDebug", "Model loaded successfully")
//
//        } catch (e: Exception) {
//            Log.e("PoseDebug", "Model FAILED to load", e)
//
//        }
//    }
//
//    // Sends a camera frame to mediapipe for asynchronous pose detection
//    fun detectAsync(mpImage: MPImage, timestampMs: Long) {
//        poseLandmarker?.detectAsync(mpImage, timestampMs)
//    }
//
//    // Frees the mediapipe resources
//    fun close() {
//        poseLandmarker?.close()
//        poseLandmarker = null
//    }
//}