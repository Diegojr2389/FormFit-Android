package com.example.formfit.MLKitPoseDetection

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.view.View
import androidx.compose.runtime.Composable
import com.example.formfit.ui.components.feedback.provideBarbellCurlFeedback
import com.example.formfit.ui.components.feedback.provideBarbellOverheadPressFeedback
import com.example.formfit.ui.components.feedback.provideBenchPressFeedback
import com.example.formfit.ui.components.feedback.provideConventionalDeadliftFeedback
import com.example.formfit.ui.components.feedback.provideGoodMorningFeedback
import com.example.formfit.ui.components.feedback.provideLegExtensionFeedback
import com.example.formfit.ui.components.feedback.providePlankFeedback
import com.example.formfit.ui.components.feedback.providePreacherCurlFeedback
import com.example.formfit.ui.components.feedback.provideSquatFeedback
import com.example.formfit.ui.components.speech.TextToSpeechManager
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import kotlin.math.max

class PoseOverlayView(context: Context, private val ttsManager: TextToSpeechManager) : View(context) {

    var pose: Pose? = null
    var imageWidth = 1
    var imageHeight = 1
    var isFrontCamera = false

    var rotationDegrees : Int = 0

    var exerciseId : String? =  ""

    // the connections between all the joints
    var connections = listOf(
        PoseLandmark.RIGHT_SHOULDER to PoseLandmark.RIGHT_ELBOW,
        PoseLandmark.RIGHT_ELBOW to PoseLandmark.RIGHT_WRIST,
        PoseLandmark.RIGHT_WRIST to PoseLandmark.RIGHT_PINKY,
        PoseLandmark.RIGHT_WRIST to PoseLandmark.RIGHT_INDEX,
        PoseLandmark.RIGHT_WRIST to PoseLandmark.RIGHT_THUMB,
        PoseLandmark.RIGHT_PINKY to PoseLandmark.RIGHT_INDEX,

        PoseLandmark.LEFT_SHOULDER to PoseLandmark.RIGHT_SHOULDER,
        PoseLandmark.LEFT_SHOULDER to PoseLandmark.LEFT_ELBOW,
        PoseLandmark.LEFT_ELBOW to PoseLandmark.LEFT_WRIST,
        PoseLandmark.LEFT_WRIST to PoseLandmark.LEFT_PINKY,
        PoseLandmark.LEFT_WRIST to PoseLandmark.LEFT_INDEX,
        PoseLandmark.LEFT_WRIST to PoseLandmark.LEFT_THUMB,
        PoseLandmark.LEFT_PINKY to PoseLandmark.LEFT_INDEX,

        PoseLandmark.LEFT_SHOULDER to PoseLandmark.LEFT_HIP,
        PoseLandmark.RIGHT_SHOULDER to PoseLandmark.RIGHT_HIP,
        PoseLandmark.RIGHT_HIP to PoseLandmark.LEFT_HIP,

        PoseLandmark.LEFT_HIP to PoseLandmark.LEFT_KNEE,
        PoseLandmark.LEFT_KNEE to PoseLandmark.LEFT_ANKLE,
        PoseLandmark.LEFT_ANKLE to PoseLandmark.LEFT_HEEL,
        PoseLandmark.LEFT_ANKLE to PoseLandmark.LEFT_FOOT_INDEX,
        PoseLandmark.LEFT_FOOT_INDEX to PoseLandmark.LEFT_HEEL,

        PoseLandmark.RIGHT_HIP to PoseLandmark.RIGHT_KNEE,
        PoseLandmark.RIGHT_KNEE to PoseLandmark.RIGHT_ANKLE,
        PoseLandmark.RIGHT_ANKLE to PoseLandmark.RIGHT_HEEL,
        PoseLandmark.RIGHT_ANKLE to PoseLandmark.RIGHT_FOOT_INDEX,
        PoseLandmark.RIGHT_HEEL to PoseLandmark.RIGHT_FOOT_INDEX
    )

    // Paint object used to draw pose landmarks
    private val circlePaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
        strokeWidth = 10f
        isAntiAlias = true
    }

    // Paint object used to draw connections between joints
    private val linePaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 6f
        isAntiAlias = true
    }

    // Draws the pose landmarks as circles and the connections between them as lines
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val landmarks = pose?.allPoseLandmarks ?: return

        landmarks.forEach { landmark ->
            if (landmark.inFrameLikelihood < 0.9) return@forEach
            val p = mapPoint(landmark.position.x, landmark.position.y)
            canvas.drawCircle(p.x, p.y, 8f, circlePaint)
        }

        val feedback = produceFeedback(pose)
        if (feedback.isNotBlank()) ttsManager.speakText(feedback)

        drawConnections(connections, canvas)
    }


    private fun mapPoint(x: Float, y: Float): PointF {

        // If rotation is 90° or 270°, the image’s width and height are swapped on screen,
        // therefore, we must swap them here when calculating scale to keep the overlay aligned correctly
        val (adjustedWidth, adjustedHeight) = getAdjustedDimensions()

        // Compute scale factor so the image fills the view while maintaining aspect ratio
        val scale = max(
            width.toFloat() / adjustedWidth,
            height.toFloat() / adjustedHeight
        )

        // Calculate the scaled image dimensions after applying scale
        val scaledWidth = adjustedWidth * scale
        val scaledHeight = adjustedHeight * scale

        // Compute horizontal and vertical offsets caused by center-cropping
        // Center cropping = Enlarges image in order to fill the whole width and height of screen
        val dx = (scaledWidth - width) / 2f
        val dy = (scaledHeight - height) / 2f

        // Scale landmark coordinates and remove cropping offset
        // Cropping offset = the part of the image that got cut off due to center cropping
        var px = x * scale - dx
        val py = y * scale - dy

        // Mirror horizontally if using the front camera
        if (isFrontCamera) {
            px = width - px
        }

        // return transformed point
        return PointF(px, py)
    }

    // If the image is sideways, flip the dimensions so drawing math still works
    private fun getAdjustedDimensions(): Pair<Int, Int> {
        return if (rotationDegrees == 90 || rotationDegrees == 270) {
            imageHeight to imageWidth
        } else {
            imageWidth to imageHeight
        }
    }

    // Function to draw the connections between the joints
    private fun drawConnections(connections: List<Pair<Int, Int>>, canvas: Canvas) {
        for ((start, end) in connections) {
            val startLandmark = pose?.getPoseLandmark(start)
            val endLandmark = pose?.getPoseLandmark(end)

            if (startLandmark == null || endLandmark == null) continue
            if (startLandmark.inFrameLikelihood < 0.95 || endLandmark.inFrameLikelihood < 0.95) continue

            val startPt = mapPoint(startLandmark.position.x, startLandmark.position.y)
            val endPt = mapPoint(endLandmark.position.x, endLandmark.position.y)

            canvas.drawLine(startPt.x, startPt.y, endPt.x, endPt.y, linePaint)
        }
    }

    private fun produceFeedback(pose: Pose? = null): String  {
        when (exerciseId) {
            "squat" -> {return  provideSquatFeedback(pose) }
            "bench-press" -> { return provideBenchPressFeedback(pose) }
            "conventional-deadlift" -> { return provideConventionalDeadliftFeedback(pose) }
            "barbell-curl" -> { return provideBarbellCurlFeedback(pose) }
            "plank" -> { return providePlankFeedback(pose) }
            "preacher-curl" -> { return providePreacherCurlFeedback(pose) }
            "leg-extension" -> { return provideLegExtensionFeedback(pose) }
            "good-morning" -> { return provideGoodMorningFeedback(pose) }
            "barbell-shoulder-overhead-press" -> { return provideBarbellOverheadPressFeedback(pose) }
            else -> return ""
        }
    }

}