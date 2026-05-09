package com.example.formfit.ui.components.feedback

import android.graphics.PointF
import com.example.formfit.models.FormFeedback
import com.example.formfit.utils.calculateAngle
import com.example.formfit.utils.determineCloserArm
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

var has_determined_closer_arm_DR = false
var closer_arm_DR = ""
var is_going_down_DR = false
var is_good_up_DR = false
var is_good_down_DR = false
var lowest_elbow_angle_DR = 360.0
var highest_elbow_angle_DR = 0.0

fun provideDumbbellRowFeedback(pose: Pose? = null): FormFeedback {
    if (pose == null) return FormFeedback("")

    if (!has_determined_closer_arm_DR) {
        closer_arm_DR = determineCloserArm(pose)
        if (closer_arm_DR != "error") {
            has_determined_closer_arm_DR = true
        }
    }

    if (closer_arm_DR == "right") {
        val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        val rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
        val rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
        val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)

        if (rightShoulder == null || rightElbow == null || rightWrist == null || rightHip == null) {
            return FormFeedback("")
        }

        return generateFeedback(rightShoulder, rightElbow, rightWrist, rightHip)
    }
    else if (closer_arm_DR == "left") {
        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
        val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
        val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)

        if (leftShoulder == null || leftElbow == null || leftWrist == null || leftHip == null) {
            return FormFeedback("")
        }

        return generateFeedback(leftShoulder, leftElbow, leftWrist, leftHip)
    }
    return FormFeedback("")
}

private fun generateFeedback(shoulder: PoseLandmark, elbow: PoseLandmark, wrist: PoseLandmark,
                             hip: PoseLandmark): FormFeedback {
    if (shoulder.inFrameLikelihood < 0.95 ||
        elbow.inFrameLikelihood < 0.95 ||
        wrist.inFrameLikelihood < 0.95 ||
        hip.inFrameLikelihood < 0.95) {
        return FormFeedback("")
    }

    val shoulderPoint = PointF(shoulder.position.x, shoulder.position.y)
    val elbowPoint = PointF(elbow.position.x, elbow.position.y)
    val wristPoint = PointF(wrist.position.x, wrist.position.y)
    val hipPoint = PointF(hip.position.x, hip.position.y)

    val elbowAngle = calculateAngle(shoulderPoint, elbowPoint, wristPoint)
    val shElHipAngle = calculateAngle(shoulderPoint, elbowPoint, hipPoint)

    if (!is_going_down_DR) {
        if (elbowAngle < lowest_elbow_angle_DR) {
            lowest_elbow_angle_DR = elbowAngle
        }

        if (elbowPoint.y < shoulderPoint.y + Y_TOLERANCE &&
            shElHipAngle < 180 - ANGLE_TOLERANCE) {
            is_good_up_DR = true
            return FormFeedback(
                message = "Good elbow drive.",
                isTop = true
            )
        }

        if (elbowAngle - lowest_elbow_angle_DR >= 20 + ANGLE_TOLERANCE) {
            is_going_down_DR = true
            lowest_elbow_angle_DR = 360.0
            if (!is_good_up_DR) {
                return FormFeedback(
                    message = "On next rep, drive elbow higher.",
                    isNextRepFeedback = true
                )
            }
            is_good_up_DR = false
        }
    }
    else {
        if (elbowAngle > highest_elbow_angle_DR) {
            highest_elbow_angle_DR = elbowAngle
        }

        // FINISH CODE - wrist and shoulder should be aligned
        if (elbowAngle >= 180 - ANGLE_TOLERANCE) {
            is_good_down_DR = true
            return FormFeedback(
                message = "Good lockout.",
                isBottom = true
            )
        }

        if (highest_elbow_angle_DR - elbowAngle >= 20 + ANGLE_TOLERANCE) {
            is_going_down_DR = false
            highest_elbow_angle_DR = 0.0
            if (!is_good_down_DR) {
                return FormFeedback(
                    message = "On next rep, extend arm more.",
                    isNextRepFeedback = true
                )
            }
            is_good_down_DR = false
        }
    }

    return FormFeedback("")
}

fun resetDumbbellRowVariables() {
    has_determined_closer_arm_DR = false
    closer_arm_DR = ""
    is_going_down_DR = false
    is_good_up_DR = false
    is_good_down_DR = false
    lowest_elbow_angle_DR = 360.0
    highest_elbow_angle_DR = 0.0
}