package com.example.formfit.ui.components.feedback

import android.graphics.PointF
import android.util.Log
import com.example.formfit.utils.calculateAngle
import com.example.formfit.utils.determineCloserSide
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

var has_determined_closer_side_pushup = false
var closer_side_pushup = ""
var is_body_straight_pushup = true
var is_first_straight_body_pushup = true // for if body is straight at the beginning
var is_going_down_pushup = true
var highest_elbow_angle_pushup = 0.0
var lowest_elbow_angle_pushup = 360.0
var is_good_up_pushup = false
var is_good_down_pushup = false

fun providePushupFeedback(pose: Pose? = null, rotationDegrees: Int): String {
    if (pose == null) return ""

    if (!has_determined_closer_side_pushup) {
        closer_side_pushup = determineCloserSide(pose)
        if (closer_side_pushup != "error") {
            has_determined_closer_side_pushup = true
        }
    }

    if (closer_side_pushup == "right") {
        val rightHeel = pose.getPoseLandmark(PoseLandmark.LEFT_HEEL)
        val rightHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        val rightShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val rightElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
        val rightWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
        val rightMouth = pose.getPoseLandmark(PoseLandmark.LEFT_MOUTH)

        if (rightHeel == null || rightHip == null || rightShoulder == null || rightElbow == null ||
            rightWrist == null || rightMouth == null) {
            return ""
        }

        return generateFeedback(rightHeel, rightHip, rightShoulder, rightElbow, rightWrist, rightMouth, rotationDegrees)
    }
    else if (closer_side_pushup == "left") {
        val leftHeel = pose.getPoseLandmark(PoseLandmark.RIGHT_HEEL)
        val leftHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
        val leftShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        val leftElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
        val leftWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
        val leftMouth = pose.getPoseLandmark(PoseLandmark.RIGHT_MOUTH)

        if (leftHeel == null || leftHip == null || leftShoulder == null || leftElbow == null ||
            leftWrist == null || leftMouth == null) {
            return ""
        }

        return generateFeedback(leftHeel, leftHip, leftShoulder, leftElbow, leftWrist, leftMouth, rotationDegrees)
    }
    return ""
}

private fun generateFeedback(heel: PoseLandmark, hip: PoseLandmark, shoulder: PoseLandmark,
                             elbow: PoseLandmark, wrist: PoseLandmark, mouth: PoseLandmark,
                             rotationDegrees: Int): String {
    if (heel.inFrameLikelihood < 0.95 ||
        hip.inFrameLikelihood < 0.95 ||
        shoulder.inFrameLikelihood < 0.95 ||
        elbow.inFrameLikelihood < 0.95 ||
        wrist.inFrameLikelihood < 0.95 ||
        mouth.inFrameLikelihood < 0.95) {
        return ""
    }

    var heelPoint = PointF(heel.position.x, heel.position.y)
    var hipPoint = PointF(hip.position.x, hip.position.y)
    var shoulderPoint = PointF(shoulder.position.x, shoulder.position.y)
    var elbowPoint = PointF(elbow.position.x, elbow.position.y)
    var wristPoint = PointF(wrist.position.x, wrist.position.y)
    var mouthPoint = PointF(mouth.position.x, mouth.position.y)
    var shoulderHeelMidPoint = PointF(((shoulderPoint.x + heelPoint.x) / 2), ((shoulderPoint.y + heelPoint.y) / 2))

    // flip coordinates if phone is in landscape mode
    if (rotationDegrees == 90 || rotationDegrees == 270) {
        heelPoint = PointF(heel.position.y, heel.position.x)
        hipPoint = PointF(hip.position.y, hip.position.x)
        shoulderPoint = PointF(shoulder.position.y, shoulder.position.x)
        elbowPoint = PointF(elbow.position.y, elbow.position.x)
        wristPoint = PointF(wrist.position.y, wrist.position.x)
        mouthPoint = PointF(mouth.position.y, mouth.position.x)
        shoulderHeelMidPoint = PointF(((shoulderPoint.y + heelPoint.y) / 2), ((shoulderPoint.x + heelPoint.x) / 2))
    }

    val hipAngle = calculateAngle(heelPoint, hipPoint, shoulderPoint)
    val elbowAngle = calculateAngle(shoulderPoint, elbowPoint, wristPoint)

    if ((!is_body_straight_pushup || is_first_straight_body_pushup) &&
        hipAngle >= 180 - ANGLE_TOLERANCE) {
        is_first_straight_body_pushup = false
        is_body_straight_pushup = true
        return "Great. Body is in a straight line."
    }

    if (is_body_straight_pushup &&
        hipAngle <= 180 - ANGLE_TOLERANCE &&
        hipPoint.y < shoulderHeelMidPoint.y - Y_TOLERANCE) {
        is_body_straight_pushup = false
        return "Lower your hips."
    }

    // ------------------------ FIX THIS -----------------------------
//    if (is_body_straight_pushup &&
//        hipAngle <= 180 - ANGLE_TOLERANCE &&
//        hipPoint.y > shoulderHeelMidPoint.y + Y_TOLERANCE) {
//        is_body_straight_pushup = false
//        return "Lift your hips."
//    }

    if (!is_going_down_pushup) {
        if (elbowAngle > highest_elbow_angle_pushup) {
            highest_elbow_angle_pushup = elbowAngle
        }

        if (elbowAngle >= 170 - ANGLE_TOLERANCE) {
            is_good_up_pushup = true
            return "Great lockout."
        }

        if (highest_elbow_angle_pushup - elbowAngle >= 20 + ANGLE_TOLERANCE) {
            is_going_down_pushup = true
            highest_elbow_angle_pushup = 0.0
            if (!is_good_up_pushup) {
                return "On next rep, extend elbows more on lockout."
            }
            is_good_up_pushup = false
        }
    }
    else {
        if (elbowAngle < lowest_elbow_angle_pushup) {
            lowest_elbow_angle_pushup = elbowAngle
        }

        if (mouthPoint.y >= wristPoint.y - Y_TOLERANCE*2) {
            is_good_down_pushup = true
            return "Great. Chest should be touching floor."
        }

        if (elbowAngle - lowest_elbow_angle_pushup >= 20 + ANGLE_TOLERANCE ) {
            is_going_down_pushup = false
            lowest_elbow_angle_pushup = 360.0
            if (!is_good_down_pushup) {
                return "On next rep, touch chest to the ground."
            }
        }
    }

    return ""
}

fun resetPushupVariables() {
    has_determined_closer_side_pushup = false
    closer_side_pushup = ""
    is_body_straight_pushup = true
    is_first_straight_body_pushup = true
    is_going_down_pushup = true
    highest_elbow_angle_pushup = 0.0
    lowest_elbow_angle_pushup = 360.0
    is_good_up_pushup = false
    is_good_down_pushup = false
}