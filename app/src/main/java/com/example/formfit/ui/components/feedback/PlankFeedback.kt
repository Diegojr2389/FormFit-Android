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
        else if (rightShoulder.inFrameLikelihood < 0.95 ||
            rightHip.inFrameLikelihood < 0.95 ||
            rightKnee.inFrameLikelihood < 0.95 ||
            rightAnkle.inFrameLikelihood < 0.95) {
            return ""
        }
        else {
            val rightShoulderPoint = PointF(rightShoulder.position.x, rightShoulder.position.y)
            val rightHipPoint = PointF(rightHip.position.x, rightHip.position.y)
            val rightKneePoint = PointF(rightKnee.position.x, rightKnee.position.y)
            val rightAnklePoint = PointF(rightAnkle.position.x, rightAnkle.position.y)

            val shHipAnkAngle = calculateAngle(rightShoulderPoint, rightHipPoint, rightAnklePoint)
            val hipknAnkAngle = calculateAngle(rightHipPoint, rightKneePoint, rightAnklePoint)

            if (!hip_feedback_triggered_plank && !knee_feedback_triggered_plank && !first_feedback_triggered) {
                first_feedback_triggered = true
                return "Nice! Hold that position."
            }

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
        }
    }
    else if (closer_side_plank == "left"){
        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        val leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
        val leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)

        if (leftShoulder == null || leftHip == null || leftKnee == null || leftAnkle == null) return ""
        else if (leftShoulder.inFrameLikelihood < 0.95 ||
            leftHip.inFrameLikelihood < 0.95 ||
            leftKnee.inFrameLikelihood < 0.95 ||
            leftAnkle.inFrameLikelihood < 0.95) {
            return ""
        }
        else {
            val leftShoulderPoint = PointF(leftShoulder.position.x, leftShoulder.position.y)
            val leftHipPoint = PointF(leftHip.position.x, leftHip.position.y)
            val leftKneePoint = PointF(leftKnee.position.x, leftKnee.position.y)
            val leftAnklePoint = PointF(leftAnkle.position.x, leftAnkle.position.y)

            val shHipAnkAngle = calculateAngle(leftShoulderPoint, leftHipPoint, leftAnklePoint)
            val hipknAnkAngle = calculateAngle(leftHipPoint, leftKneePoint, leftAnklePoint)

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
        }
    }
    return ""
}