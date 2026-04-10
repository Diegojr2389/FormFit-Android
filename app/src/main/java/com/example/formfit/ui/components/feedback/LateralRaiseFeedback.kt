package com.example.formfit.ui.components.feedback

import android.graphics.PointF
import android.util.Log
import com.example.formfit.utils.Point3F
import com.example.formfit.utils.calculateAngle
import com.example.formfit.utils.calculateZAngle
import com.example.formfit.utils.determineCloserArm
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

var has_determined_closer_arm_LR = false
var closer_arm_LR = ""
var is_going_down_LR = false
var lowest_hipShWrAngle = 360.0
var highest_hipShElAngle = 0.0
var is_good_up_LR = false
var is_good_down_LR = false

fun provideLateralRaiseFeedback(pose: Pose? = null): String {
    if (pose == null) return ""

    if (!has_determined_closer_arm_LR) {
        closer_arm_LR = determineCloserArm(pose)
        if (closer_arm_LR != "error") {
            has_determined_closer_arm_LR = true
        }
    }

    if (closer_arm_LR == "right") {
        val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
        val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        val rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
        val rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)

        if (rightHip == null || rightShoulder == null || rightElbow == null || rightWrist == null) {
            return ""
        }

        return generateFeedback(rightHip, rightShoulder, rightElbow, rightWrist)
    }
    else if (closer_arm_LR == "left") {
        val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
        val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)

        if (leftHip == null || leftShoulder == null || leftElbow == null || leftWrist == null) {
            return ""
        }

        return generateFeedback(leftHip, leftShoulder, leftElbow, leftWrist)
    }
    return ""
}

private fun generateFeedback(hip: PoseLandmark, shoulder: PoseLandmark, elbow: PoseLandmark,
                            wrist: PoseLandmark): String {
    if (hip.inFrameLikelihood < 0.95 ||
        shoulder.inFrameLikelihood < 0.95 ||
        elbow.inFrameLikelihood < 0.95 ||
        wrist.inFrameLikelihood < 0.95) {
        return ""
    }

    val hipPoint = PointF(hip.position.x, hip.position.y)
    val shoulderPoint = PointF(shoulder.position.x, shoulder.position.y)
    val elbowPoint = PointF(elbow.position.x, elbow.position.y)
    val wristPoint = PointF(wrist.position.x, wrist.position.y)

    val shoulderPoint3D = Point3F(shoulder.position.x, shoulder.position.y, shoulder.position3D.z)
    val elbowPoint3D = Point3F(elbow.position.x, elbow.position.y, elbow.position3D.z)
    val wristPoint3D = Point3F(wrist.position.x, wrist.position.y, wrist.position3D.z)

    val frontElbowAngle = calculateZAngle(shoulderPoint3D, elbowPoint3D, wristPoint3D)
    val hipShElAngle = calculateAngle(hipPoint, shoulderPoint, elbowPoint)
    val hipShWrAngle = calculateAngle(hipPoint, shoulderPoint, wristPoint)

    /////////// EXPERIMENT LATER/////////

//    Log.d("Feedback", "ELBOW: $frontElbowAngle")
//    if (frontElbowAngle >= 160 + ANGLE_TOLERANCE) {
//        Log.d("Feedback", "TOO STRAIGHT")
//        return "Arms are too straight, bend elbows more"
//    }
//    if (frontElbowAngle <= 160 - ANGLE_TOLERANCE) {
//        Log.d("Feedback", "TOO BENT")
//        return "Arms are too bent, bend elbows less"
//    }
//    if (frontElbowAngle >= 160 - ANGLE_TOLERANCE && frontElbowAngle <= 160 + ANGLE_TOLERANCE) {
//        Log.d("Feedback", "GOOD FLEXION")
//        return "Good job. Good elbow flexion."
//    }

    if (!is_going_down_LR) {
        if (hipShElAngle > highest_hipShElAngle) {
            highest_hipShElAngle = hipShElAngle
        }

        if (hipShElAngle >= 90 - ANGLE_TOLERANCE) {
            is_good_up_LR = true
            return "Perfect. Stay consistent with that arm elevation."
        }
        // going down
        if (highest_hipShElAngle - hipShElAngle >= 20 + ANGLE_TOLERANCE) {
            is_going_down_LR = true
            highest_hipShElAngle = 0.0
            if (!is_good_up_LR) {
                return "On next rep, raise elbows to shoulder height."
            }
            is_good_up_LR = false
        }
    }
    else {
        if (hipShWrAngle < lowest_hipShWrAngle) {
            lowest_hipShWrAngle = hipShWrAngle
        }
        if (hipShWrAngle <= 10 + ANGLE_TOLERANCE) {
            is_good_down_LR = true
            return "Perfect. Arms returned to a neutral position."
        }
        // going up
        if (hipShWrAngle - lowest_hipShWrAngle >= 20 + ANGLE_TOLERANCE) {
            is_going_down_LR = false
            lowest_hipShWrAngle = 360.0
            if (!is_good_down_LR) {
                return "On next rep, lower hands closer to hips"
            }
            is_good_down_LR = false
        }
    }
    return ""
}

fun resetLRVariables() {
    has_determined_closer_arm_LR = false
    closer_arm_LR = ""
    is_going_down_LR = false
    lowest_hipShWrAngle = 360.0
    highest_hipShElAngle = 0.0
    is_good_up_LR = false
    is_good_down_LR = false
}