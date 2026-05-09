package com.example.formfit.ui.components.feedback

import android.graphics.PointF
import android.util.Log
import com.example.formfit.models.FormFeedback
import com.example.formfit.utils.calculateAngle
import com.example.formfit.utils.determineCloserSide
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

var has_determined_closer_side_plank = false
var closer_side_plank = ""

var knee_feedback_triggered_plank = false
var hip_feedback_triggered_plank = false
var first_feedback_triggered = false

fun providePlankFeedback(pose: Pose? = null): FormFeedback {
    if (pose == null) return FormFeedback("")

    if (!has_determined_closer_side_plank) {
        closer_side_plank = determineCloserSide(pose)
        if (closer_side_plank != "error") has_determined_closer_side_plank = true
    }

    if (closer_side_plank == "right") {
        val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
        val rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
        val rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)

        if (rightShoulder == null || rightHip == null || rightKnee == null || rightAnkle == null) return FormFeedback("")

        return generateFeedback(rightShoulder, rightHip, rightKnee, rightAnkle)
    }
    else if (closer_side_plank == "left"){
        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        val leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
        val leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)

        if (leftShoulder == null || leftHip == null || leftKnee == null || leftAnkle == null) return FormFeedback("")

        return generateFeedback(leftShoulder, leftHip, leftKnee, leftAnkle)
    }
    return FormFeedback("")
}

private fun generateFeedback(shoulder: PoseLandmark, hip: PoseLandmark, knee: PoseLandmark, ankle: PoseLandmark): FormFeedback {
    if (shoulder.inFrameLikelihood < 0.95 ||
        hip.inFrameLikelihood < 0.95 ||
        knee.inFrameLikelihood < 0.95 ||
        ankle.inFrameLikelihood < 0.95) {
        return FormFeedback("")
    }

    val shoulderPoint = PointF(shoulder.position.x, shoulder.position.y)
    val hipPoint = PointF(hip.position.x, hip.position.y)
    val kneePoint = PointF(knee.position.x, knee.position.y)
    val anklePoint = PointF(ankle.position.x, ankle.position.y)

    val shHipAnkAngle = calculateAngle(shoulderPoint, hipPoint, anklePoint)
    val hipknAnkAngle = calculateAngle(hipPoint, kneePoint, anklePoint)

    if (shHipAnkAngle < 180 - ANGLE_TOLERANCE) {
        hip_feedback_triggered_plank = true
        return FormFeedback(
            message = "Hips are too high. Lower them.",
            isBadFeedback = true
        )
    }
    if (shHipAnkAngle > 180) {
        hip_feedback_triggered_plank = true
        return FormFeedback(
            message = "Hips are too low. Lift them.",
            isBadFeedback = true
        )
    }
    if (hipknAnkAngle < 170 - ANGLE_TOLERANCE) {
        knee_feedback_triggered_plank = true
        return FormFeedback(
            message = "Your knees are bending. Straighten them.",
            isBadFeedback = true
        )
    }
    if (shHipAnkAngle >= 180 - ANGLE_TOLERANCE && shHipAnkAngle <= 180 + ANGLE_TOLERANCE && hip_feedback_triggered_plank) {
        hip_feedback_triggered_plank = false
        return FormFeedback(
            message = "Perfect. Your hips are now aligned.",
            isGoodFeedback = true
        )
    }
    if (hipknAnkAngle > 170 - ANGLE_TOLERANCE && knee_feedback_triggered_plank) {
        knee_feedback_triggered_plank = false
        return FormFeedback(
            message = "Perfect. Your knees are now aligned.",
            isGoodFeedback = true
        )
    }
    if (!first_feedback_triggered) {
        first_feedback_triggered = true
        return FormFeedback(
            message = "Nice! Hold that position.",
            isGoodFeedback = true
        )
    }

    return FormFeedback("")
}

fun resetPlankVariables() {
    has_determined_closer_side_plank = false
    closer_side_plank = ""
    knee_feedback_triggered_plank = false
    hip_feedback_triggered_plank = false
    first_feedback_triggered = false
}