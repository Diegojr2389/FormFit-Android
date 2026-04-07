//package com.example.formfit.mediapipe
//
//import android.graphics.Bitmap
//import android.graphics.Matrix
//import androidx.camera.core.ImageProxy
//import com.google.mediapipe.framework.image.BitmapImageBuilder
//import com.google.mediapipe.framework.image.MPImage
//import com.google.mediapipe.tasks.vision.core.RunningMode
//
//// Code from "https://github.com/google-ai-edge/mediapipe-samples/blob/main/examples/pose_landmarker/android/app/src/main/java/com/google/mediapipe/examples/poselandmarker/PoseLandmarkerHelper.kt#L26"
//fun convertImageProxyToMPImage(
//    imageProxy: ImageProxy,
//    isFrontCamera: Boolean,
//    runningMode: RunningMode = RunningMode.LIVE_STREAM
//): MPImage {
//
//    val bitmapBuffer =
//        Bitmap.createBitmap(
//            imageProxy.width,
//            imageProxy.height,
//            Bitmap.Config.ARGB_8888
//        )
//
//    imageProxy.use { bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer) }
//    imageProxy.close()
//
//    val matrix = Matrix().apply {
//        // Rotate the frame received from the camera to be in the same direction as it'll be shown
//        postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
//
//        // flip image if user use front camera
//        if (isFrontCamera) {
//            postScale(
//                -1f,
//                1f,
//                imageProxy.width.toFloat(),
//                imageProxy.height.toFloat()
//            )
//        }
//    }
//    val rotatedBitmap = Bitmap.createBitmap(
//        bitmapBuffer, 0, 0, bitmapBuffer.width, bitmapBuffer.height,
//        matrix, true
//    )
//
//    // Convert the input Bitmap object to an MPImage object to run inference and return
//    return BitmapImageBuilder(rotatedBitmap).build()
//}