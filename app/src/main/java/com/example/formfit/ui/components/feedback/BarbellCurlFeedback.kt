package com.example.formfit.ui.components.feedback

import android.graphics.PointF
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import com.example.formfit.models.FormFeedback
import com.example.formfit.ui.theme.FormFitTheme
import com.example.formfit.utils.calculateAngle
import com.example.formfit.utils.determineCloserArm
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

var lowest_angle_barbell_curl = 360.0
var highest_angle_barbell_curl = 0.0
var closer_arm_barbell_curl = ""
var has_determined_closer_arm_barbell_curl = false
var is_going_down_barbell_curl = false

val ANGLE_TOLERANCE = 15.0f

var is_good_up_barbell_curl_rep = false
var is_good_down_barbell_curl_rep = false

var is_elbow_too_forward_barbell_curl = false

fun provideBarbellCurlFeedback(pose: Pose? = null): FormFeedback {
    if (pose == null) return FormFeedback("")

    if (!has_determined_closer_arm_barbell_curl) {
        closer_arm_barbell_curl = determineCloserArm(pose)
        if (closer_arm_barbell_curl != "error") has_determined_closer_arm_barbell_curl = true
    }

    if(closer_arm_barbell_curl == "right") {
        val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        val rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
        val rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
        val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)

        if (rightShoulder == null || rightElbow == null || rightWrist == null || rightHip == null) {
            return FormFeedback("")
        }

        return generateFeedback(rightShoulder, rightElbow, rightWrist, rightHip)
    }

    else if (closer_arm_barbell_curl == "left") {
        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
        val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
        val leftHip = pose.getPoseLandmark((PoseLandmark.LEFT_HIP))

        if (leftShoulder == null || leftElbow == null || leftWrist == null || leftHip == null) {
            return FormFeedback("")
        }

        return generateFeedback(leftShoulder, leftElbow, leftWrist, leftHip)
    }
    return FormFeedback("")
}

private fun generateFeedback(shoulder: PoseLandmark, elbow: PoseLandmark, wrist: PoseLandmark, hip: PoseLandmark): FormFeedback {
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

    val currentArmAngle = calculateAngle(shoulderPoint, elbowPoint, wristPoint)
    val hShElAngle = calculateAngle(hipPoint, shoulderPoint, elbowPoint)

    if (hShElAngle > (15 + ANGLE_TOLERANCE)) {
        is_elbow_too_forward_barbell_curl = true
        return FormFeedback(
            message = "Elbow is too forward",
            isBadFeedback = true
        )
    }

    if (is_elbow_too_forward_barbell_curl && hShElAngle < 15) {
        is_elbow_too_forward_barbell_curl = false
        return FormFeedback(
            message = "Good elbow position",
            isGoodFeedback = true
        )
    }

    // going up
    if (!is_going_down_barbell_curl) {
        if (currentArmAngle < lowest_angle_barbell_curl) {
            lowest_angle_barbell_curl = currentArmAngle
        }

        // curl should reach about 60 degrees at the top of the rep
        if (currentArmAngle < 60 + ANGLE_TOLERANCE) {
            is_good_up_barbell_curl_rep = true
            return FormFeedback(
                message = "Great, full range!",
                isTop = true
            )
        }

        // is going down
        if (currentArmAngle - lowest_angle_barbell_curl > 50) {
            is_going_down_barbell_curl = true
            lowest_angle_barbell_curl = 360.0
            if (!is_good_up_barbell_curl_rep) {
                return FormFeedback(
                    message = "On next rep, curl the bar higher.",
                    isNextRepFeedback = true
                )
            }
            is_good_up_barbell_curl_rep = false
        }
    }
    else {
        if (currentArmAngle > highest_angle_barbell_curl) {
            highest_angle_barbell_curl = currentArmAngle
        }
        if (currentArmAngle >= 170 - ANGLE_TOLERANCE) {
            is_good_down_barbell_curl_rep = true
            return FormFeedback(
                message = "Great, full extension!",
                isBottom = true
            )
        }
        if (highest_angle_barbell_curl - currentArmAngle >= 30 + ANGLE_TOLERANCE) {
            is_going_down_barbell_curl = false
            highest_angle_barbell_curl = 0.0
            if (!is_good_down_barbell_curl_rep) {
                return FormFeedback(
                    message = "On next rep, fully extend arms",
                    isNextRepFeedback = true
                )
            }
            is_good_down_barbell_curl_rep = false
        }
    }
    return FormFeedback("")
}

fun resetBarbellCurlVariables() {
    lowest_angle_barbell_curl = 360.0
    highest_angle_barbell_curl = 0.0
    closer_arm_barbell_curl = ""
    has_determined_closer_arm_barbell_curl = false
    is_going_down_barbell_curl = false
    is_good_up_barbell_curl_rep = false
    is_good_down_barbell_curl_rep = false
    is_elbow_too_forward_barbell_curl = false
}