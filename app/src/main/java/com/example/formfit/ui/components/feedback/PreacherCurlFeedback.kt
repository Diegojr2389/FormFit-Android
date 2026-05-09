package com.example.formfit.ui.components.feedback

import android.graphics.PointF
import android.util.Log
import com.example.formfit.models.FormFeedback
import com.example.formfit.utils.calculateAngle
import com.example.formfit.utils.determineCloserArm
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

var has_determined_closer_arm_preacher_curl = false
var closer_arm_preacher_curl = ""
var lowest_arm_angle_preacher = 360.0
var highest_arm_angle_preacher = 0.0
var is_going_down_preacher = false
var is_good_up_preacher = false
fun providePreacherCurlFeedback(pose: Pose? = null): FormFeedback {
    if (pose == null) return FormFeedback("")

    if (!has_determined_closer_arm_preacher_curl) {
        closer_arm_preacher_curl = determineCloserArm(pose)
        if (closer_arm_preacher_curl != "error") {
            has_determined_closer_arm_preacher_curl = true
        }
    }

    if (closer_arm_preacher_curl == "right") {
        val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        val rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
        val rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)

        if (rightShoulder == null|| rightElbow == null || rightWrist == null) return FormFeedback("")

        return generateFeedback(rightShoulder, rightElbow, rightWrist)
    }
    else if(closer_arm_preacher_curl == "left") {
        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
        val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)

        if (leftShoulder == null|| leftElbow == null || leftWrist == null) return FormFeedback("")

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

    val armAngle = calculateAngle(shoulderPoint, elbowPoint, wristPoint)

    if (!is_going_down_preacher) {
        if (armAngle < lowest_arm_angle_preacher) {
            lowest_arm_angle_preacher = armAngle
        }
        if (armAngle <= 80) {
            is_good_up_preacher = true
            return FormFeedback(
                message = "Perfect. Good Squeeze.",
                isTop = true
            )
        }
        // going down
        if (armAngle - lowest_arm_angle_preacher >= 40 + ANGLE_TOLERANCE) {
            is_going_down_preacher = true
            if (!is_good_up_preacher) {
                return FormFeedback(
                    message = "On next rep, lift higher and squeeze at the top",
                    isNextRepFeedback = true
                )
            }
        }
    }
    else {
        if (armAngle > highest_arm_angle_preacher) {
            highest_arm_angle_preacher = armAngle
        }
        // extended arms well
        if (armAngle > 160 - ANGLE_TOLERANCE) {
            is_going_down_preacher = false
            is_good_up_preacher = false
            lowest_arm_angle_preacher = 360.0
            return FormFeedback(
                message = "Perfect. Full extension!",
                isBottom = true
            )
        }
        // did not extend arms all the way down (is now going up)
        if (highest_arm_angle_preacher - armAngle >= 30 + ANGLE_TOLERANCE) {
            is_going_down_preacher = false
            is_good_up_preacher = false
            lowest_arm_angle_preacher = 360.0
            return FormFeedback(
                message = "On next rep, extend your arms all the way down.",
                isNextRepFeedback = true
            )
        }
    }
    return FormFeedback("")
}

fun resetPreacherCurlVariables() {
    has_determined_closer_arm_preacher_curl = false
    closer_arm_preacher_curl = ""
    lowest_arm_angle_preacher = 360.0
    highest_arm_angle_preacher = 0.0
    is_going_down_preacher = false
    is_good_up_preacher = false
}