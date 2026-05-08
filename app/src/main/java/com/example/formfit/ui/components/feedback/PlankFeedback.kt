package com.example.formfit.ui.components.feedback

import android.graphics.PointF
import android.util.Log
import com.example.formfit.utils.calculateAngle
import com.example.formfit.utils.determineCloserSide
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

var has_determined_closer_side_plank = false
var closer_side_plank = ""

var knee_feedback_triggered_plank = false
var hip_feedback_triggered_plank = false
var first_feedback_triggered = false

fun providePlankFeedback(pose: Pose? = null): String {
    if (pose == null) return ""

    if (!has_determined_closer_side_plank) {
        closer_side_plank = determineCloserSide(pose)
        if (closer_side_plank != "error") has_determined_closer_side_plank = true
    }

    if (closer_side_plank == "right") {
        val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
        val rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
        val rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)

        if (rightShoulder == null || rightHip == null || rightKnee == null || rightAnkle == null) return ""

        return generateFeedback(rightShoulder, rightHip, rightKnee, rightAnkle)
    }
    else if (closer_side_plank == "left"){
        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        val leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
        val leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)

        if (leftShoulder == null || leftHip == null || leftKnee == null || leftAnkle == null) return ""

        return generateFeedback(leftShoulder, leftHip, leftKnee, leftAnkle)
    }
    return ""
}

private fun generateFeedback(shoulder: PoseLandmark, hip: PoseLandmark, knee: PoseLandmark, ankle: PoseLandmark): String {
    if (shoulder.inFrameLikelihood < 0.95 ||
        hip.inFrameLikelihood < 0.95 ||
        knee.inFrameLikelihood < 0.95 ||
        ankle.inFrameLikelihood < 0.95) {
        return ""
    }

    val shoulderPoint = PointF(shoulder.position.x, shoulder.position.y)
    val hipPoint = PointF(hip.position.x, hip.position.y)
    val kneePoint = PointF(knee.position.x, knee.position.y)
    val anklePoint = PointF(ankle.position.x, ankle.position.y)

    val shHipAnkAngle = calculateAngle(shoulderPoint, hipPoint, anklePoint)
    val hipknAnkAngle = calculateAngle(hipPoint, kneePoint, anklePoint)

    if (shHipAnkAngle < 180 - ANGLE_TOLERANCE) {
        hip_feedback_triggered_plank = true
        return "Hips are too high. Lower them."
    }
    if (shHipAnkAngle > 180) {
        hip_feedback_triggered_plank = true
        return "Hips are too low. Lift them."
    }
    if (hipknAnkAngle < 170 - ANGLE_TOLERANCE) {
        knee_feedback_triggered_plank = true
        return "Your knees are bending. Straighten them."
    }
    if (shHipAnkAngle >= 180 - ANGLE_TOLERANCE && shHipAnkAngle <= 180 + ANGLE_TOLERANCE && hip_feedback_triggered_plank) {
        hip_feedback_triggered_plank = false
        return "Perfect. Your hips are now aligned. Hold that position."
    }
    if (hipknAnkAngle > 170 - ANGLE_TOLERANCE && knee_feedback_triggered_plank) {
        knee_feedback_triggered_plank = false
        return "Perfect. Your knees are now aligned. Hold that position."
    }
    if (!first_feedback_triggered) {
        first_feedback_triggered = true
        return "Nice! Hold that position."
    }

    return ""
}

fun resetPlankVariables() {
    has_determined_closer_side_plank = false
    closer_side_plank = ""
    knee_feedback_triggered_plank = false
    hip_feedback_triggered_plank = false
    first_feedback_triggered = false
}