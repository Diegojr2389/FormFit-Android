package com.example.formfit.ui.components.feedback

import android.graphics.PointF
import android.util.Log
import com.example.formfit.models.FormFeedback
import com.example.formfit.utils.calculateAngle
import com.example.formfit.utils.determineCloserArm
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

var has_determined_closer_arm_pullup = false
var closer_arm_pullup = ""
var is_going_down_pullup = false
var is_good_up_pullup = false
var is_good_down_pullup = false
var lowest_shoulder_Y_pullup = 2000.0f
var highest_elbow_angle_pullup = 0.0

fun providePullupFeedback(pose: Pose? = null): FormFeedback {
    if (pose == null) return FormFeedback("")

    if (!has_determined_closer_arm_pullup) {
        closer_arm_pullup = determineCloserArm(pose)
        if (closer_arm_pullup != "error") {
            has_determined_closer_arm_pullup = true
        }
    }

    if (closer_arm_pullup == "right") {
        val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        val rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
        val rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)

        if (rightShoulder == null || rightElbow == null || rightWrist == null) return FormFeedback("")

        return generateFeedback(rightShoulder, rightElbow, rightWrist)
    }
    else if (closer_arm_pullup == "left") {
        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
        val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)

        if (leftShoulder == null || leftElbow == null || leftWrist == null) return FormFeedback("")

        return generateFeedback(leftShoulder, leftElbow, leftWrist)
    }
    return FormFeedback("")
}

private fun generateFeedback(shoulder: PoseLandmark, elbow: PoseLandmark, wrist: PoseLandmark): FormFeedback {
    if (shoulder.inFrameLikelihood < 0.95 ||
        elbow.inFrameLikelihood < 0.95 ||
        wrist.inFrameLikelihood < 0.95) {
        return FormFeedback("")
    }

    val shoulderPoint = PointF(shoulder.position.x, shoulder.position.y)
    val elbowPoint = PointF(elbow.position.x, elbow.position.y)
    val wristPoint = PointF(wrist.position.x, wrist.position.y)

    val elbowAngle = calculateAngle(shoulderPoint, elbowPoint, wristPoint)

    if (!is_going_down_pullup) {
        if (shoulderPoint.y < lowest_shoulder_Y_pullup) {
            lowest_shoulder_Y_pullup = shoulderPoint.y        }

        // good rep at top
        if (shoulderPoint.y <= wristPoint.y + Y_TOLERANCE) {
            is_good_up_pullup = true
            return FormFeedback(
                message = "Perfect. Chin over bar!",
                isTop = true
            )
        }

        if (shoulderPoint.y - lowest_shoulder_Y_pullup >= 30 + Y_TOLERANCE) {
            is_going_down_pullup = true
            lowest_shoulder_Y_pullup = 2000.0f
            if (!is_good_up_pullup) {
                return FormFeedback(
                    message = "On next rep, pull yourself until wrists align with shoulders.",
                    isNextRepFeedback = true
                )
            }
            is_good_up_pullup = false
        }
    }
    else {
        if (elbowAngle > highest_elbow_angle_pullup) {
            highest_elbow_angle_pullup = elbowAngle
        }

        // good rep at bottom
        if (elbowAngle >= 180 - ANGLE_TOLERANCE) {
            is_good_down_pullup = true
            return FormFeedback(
                message = "Perfect. Full extension!",
                isBottom = true
            )
        }

        if (highest_elbow_angle_pullup - elbowAngle >= 20 + ANGLE_TOLERANCE) {
            is_going_down_pullup = false
            highest_elbow_angle_pullup = 0.0
            if (!is_good_down_pullup) {
                return FormFeedback(
                    message = "On next rep, lower your body more.",
                    isNextRepFeedback = true
                )
            }
            is_good_down_pullup = false
        }
    }

    return FormFeedback("")
}

fun resetPullupVariables() {
    has_determined_closer_arm_pullup = false
    closer_arm_pullup = ""
    is_going_down_pullup = false
    is_good_up_pullup = false
    is_good_down_pullup = false
    lowest_shoulder_Y_pullup = 2000.0f
    highest_elbow_angle_pullup = 0.0
}