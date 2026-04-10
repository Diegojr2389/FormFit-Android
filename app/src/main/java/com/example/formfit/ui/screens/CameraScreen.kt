package com.example.formfit.ui.screens

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.formfit.MLKitPoseDetection.ImageAnalyzer
import com.example.formfit.MLKitPoseDetection.PoseOverlayView
import com.example.formfit.ui.components.feedback.resetBSOPVariables
import com.example.formfit.ui.components.feedback.resetLRVariables
import com.example.formfit.ui.components.speech.SpeechToTextManager
import com.example.formfit.ui.components.speech.TextToSpeechManager
import java.util.concurrent.Executors

// Composable screen that sets up the camera and the pose overlay
@Composable
@androidx.camera.core.ExperimentalGetImage
fun CameraScreen(exerciseId: String?) {
    when (exerciseId) {
        "barbell-shoulder-overhead-press" -> resetBSOPVariables()
        "lateral-raise" -> resetLRVariables()
    }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    val speechManager = remember {
        SpeechToTextManager(context)
    }

    val ttsManager = remember { TextToSpeechManager(context) }
    // Initialize TTS Manager only once, after composable enters composition
    LaunchedEffect(Unit) {
        ttsManager.initializeTTS()
    }

    val overlay = remember { PoseOverlayView(context, ttsManager) }

    LaunchedEffect(Unit) {
        speechManager.startListening() { text ->
            if (text.contains("start", ignoreCase = true)) {
                overlay.exerciseId = exerciseId
            }
        }
    }
    // Clean up when composable (Camera Screen) leaves composition
    DisposableEffect(Unit) {
        onDispose { ttsManager.shutdown() }
    }

    // Clean up when composable (Camera Screen) leaves composition
    DisposableEffect(Unit) {
        onDispose {
            ttsManager.shutdown()
            speechManager.destroy()
        }
    }

    // display camera preview inside Compose
    AndroidView(
        factory = { previewView },
        modifier = Modifier.fillMaxSize()
    )

    // display pose overlay on top of the camera preview
    AndroidView(
        factory = { overlay },
        modifier = Modifier.fillMaxSize()
    )

    // Launch camera setup once, when the composable enters composition
    LaunchedEffect(Unit) {
        val cameraProvider = ProcessCameraProvider.getInstance(context).get()

        // select front camera if available, else fall back to back camera
        val hasFrontCamera = cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)
        val selector = if (hasFrontCamera) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }

        val isFront = selector == CameraSelector.DEFAULT_FRONT_CAMERA

        // build camera preview use case and attach it to the PreviewView
        val preview = Preview.Builder().build().apply {
            setSurfaceProvider(previewView.surfaceProvider)
        }

        // Create analyzer to process frames and send pose data to overlay
        val analyzer = ImageAnalyzer { pose, imgW, imgH, rotationDeg ->
            overlay.pose = pose
            overlay.imageWidth = imgW
            overlay.imageHeight = imgH
            overlay.isFrontCamera = isFront
            overlay.rotationDegrees = rotationDeg
            overlay.invalidate()
        }

        // Configure image analysis use case with latest frame strategy
        // STRATEGY_KEEP_ONLY_LATEST drops old frames to be able to process new ones
        // in order to keep camera responsive
        val analysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .apply {
                setAnalyzer(Executors.newSingleThreadExecutor(), analyzer)
            }

        // unbind any previous use cases before rebinding
        // In order to start clean for the next binding preview and analysis
        // Basically, to prevent crashes
        cameraProvider.unbindAll()

        // Bind preview and analysis use cases to lifecycle
        // Basically, it turns on the camera, enables the pose detection and updates the overlay
        cameraProvider.bindToLifecycle(
            lifecycleOwner, // keeps camera running while screen is alive
            selector, // back or front camera
            preview, // use case that shows camera feed on screen
            analysis // use case that processes each frame
        )
    }
}