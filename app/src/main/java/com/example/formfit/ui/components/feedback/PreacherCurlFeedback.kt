package com.example.formfit.ui.components.feedback

import android.graphics.PointF
import android.util.Log
import com.example.formfit.utils.calculateAngle
import com.example.formfit.utils.determineCloserArm
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

var has_determined_closer_arm_preacher_curl = false
var closer_arm_preacher_curl = ""
var lowest_arm_angle_preacher = 360.0
var is_going_down_preacher = false
var is_good_up_preacher = false
var angle_check = 0.0
fun providePreacherCurlFeedback(pose: Pose? = null): String {
    if (pose == null) return ""

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

        if (rightShoulder == null|| rightElbow == null || rightWrist == null) return ""

        return generateFeedback(rightShoulder, rightElbow, rightWrist)
    }
    else if(closer_arm_preacher_curl == "left") {
        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
        val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)

        if (leftShoulder == null|| leftElbow == null || leftWrist == null) return ""

        return generateFeedback(leftShoulder, leftElbow, leftWrist)
    }
    return ""
}

private fun generateFeedback(shoulder: PoseLandmark, elbow: PoseLandmark, wrist: PoseLandmark): String {
    if (shoulder.inFrameLikelihood < 0.95 ||
        elbow.inFrameLikelihood < 0.95 ||
        wrist.inFrameLikelihood < 0.95) {
        return ""
    }

    val shoulderPoint = PointF(shoulder.position.x, shoulder.position.y)
    val elbowPoint = PointF(elbow.position.x, elbow.position.y)
    val wristPoint = PointF(wrist.position.x, wrist.position.y)

    val armAngle = calculateAngle(shoulderPoint, elbowPoint, wristPoint)
    Log.d("Feedback", "ANGLE: $armAngle")

    if (!is_going_down_preacher) {
        if (armAngle < lowest_arm_angle_preacher) {
            lowest_arm_angle_preacher = armAngle
        }
        if (armAngle <= 80) {
            is_good_up_preacher = true
            return "Perfect. Keep it controlled."
        }
        if (armAngle - lowest_arm_angle_preacher >= 40 + ANGLE_TOLERANCE) {
            is_going_down_preacher = true
            if (!is_good_up_preacher) return "On next rep, lift higher and squeeze at the top"
        }
    }
    else {
        if (armAngle > angle_check) {
            angle_check = armAngle
        }
        // extended arms all the way down
        if (armAngle > 160 - ANGLE_TOLERANCE) {
            is_going_down_preacher = false
            is_good_up_preacher = false
            lowest_arm_angle_preacher = 360.0
            return "Good. Full range of motion."
        }
        // did not extend arms all the way down (is now going up)
        if (angle_check - armAngle >= 30 + ANGLE_TOLERANCE) {
            is_going_down_preacher = false
            is_good_up_preacher = false
            lowest_arm_angle_preacher = 360.0
            return "On next rep, extend your arms all the way down."
        }
    }
    return ""
}