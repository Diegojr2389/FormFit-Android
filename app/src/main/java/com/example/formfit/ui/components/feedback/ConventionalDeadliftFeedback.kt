package com.example.formfit.ui.components.feedback

import android.graphics.PointF
import android.util.Log
import com.example.formfit.utils.calculateAngle
import com.example.formfit.utils.determineCloserSide
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

var has_determined_closer_side_CDL = false
var closer_side_CDL = ""
var is_going_down_CDL = false
var is_good_up_CDL = false
var highest_hip_angle_CDL = 0.0
var lowest_hip_angle_CDL = 360.0
var is_bar_aligned_with_midfoot_CDL = false
var X_TOLERANCE = 20

fun provideConventionalDeadliftFeedback(pose: Pose? = null): String {
    if (pose == null) return ""
    if (!has_determined_closer_side_CDL) {
        closer_side_CDL = determineCloserSide(pose)
        if (closer_side_CDL != "error") {
            has_determined_closer_side_CDL = true
        }

    }

    if (closer_side_CDL == "right") {
        val rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
        val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
        val rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
        val rightHeel = pose.getPoseLandmark(PoseLandmark.RIGHT_HEEL)
        val rightFoot = pose.getPoseLandmark(PoseLandmark.RIGHT_FOOT_INDEX)

        if (rightWrist == null || rightShoulder == null || rightHip == null || rightKnee == null ||
            rightHeel == null || rightFoot == null) {
            return ""
        }
        return generateFeedback(rightWrist, rightShoulder, rightHip, rightKnee, rightHeel, rightFoot)
    }
    else if (closer_side_CDL == "left") {
        val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        val leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
        val leftHeel = pose.getPoseLandmark(PoseLandmark.LEFT_HEEL)
        val leftFoot = pose.getPoseLandmark(PoseLandmark.LEFT_FOOT_INDEX)

        if (leftWrist == null || leftShoulder == null || leftHip == null || leftKnee == null ||
            leftHeel == null || leftFoot == null) {
            return ""
        }
        return generateFeedback(leftWrist, leftShoulder, leftHip, leftKnee, leftHeel, leftFoot)
    }
    return ""
}
private fun generateFeedback(wrist: PoseLandmark, shoulder: PoseLandmark, hip: PoseLandmark,
                             knee: PoseLandmark, heel: PoseLandmark, foot: PoseLandmark): String {
    if (wrist.inFrameLikelihood < 0.95 ||
        knee.inFrameLikelihood < 0.95 ||
        heel.inFrameLikelihood < 0.95 ||
        foot.inFrameLikelihood < 0.95) {
        return ""
    }

    val wristPoint = PointF(wrist.position.x, wrist.position.y)
    val shoulderPoint = PointF(shoulder.position.x, shoulder.position.y)
    val hipPoint = PointF(hip.position.x, hip.position.y)
    val kneePoint = PointF(knee.position.x, knee.position.y)
    val heelPoint = PointF(heel.position.x, heel.position.y)
    val footPoint = PointF(foot.position.x, foot.position.y)
    val midFootX = (heelPoint.x + footPoint.x) / 2

    val hipAngle = calculateAngle(heelPoint, hipPoint, shoulderPoint)

    if (!is_bar_aligned_with_midfoot_CDL &&
        wristPoint.x <= midFootX + X_TOLERANCE &&
        wristPoint.x >= midFootX - X_TOLERANCE) {
        is_bar_aligned_with_midfoot_CDL = true
        return "Good bar and midfoot alignment"
    }

    if (is_bar_aligned_with_midfoot_CDL &&
        wristPoint.x >= midFootX + X_TOLERANCE) {
        is_bar_aligned_with_midfoot_CDL = false
        if (closer_side_CDL == "right") {
            return "Bar is too forward"
        }
        if (closer_side_CDL == "left") {
            return "Bar is too back"
        }
    }

    if (is_bar_aligned_with_midfoot_CDL &&
        wristPoint.x <= midFootX - X_TOLERANCE) {
        is_bar_aligned_with_midfoot_CDL = false
        if (closer_side_CDL == "right") {
            return "Bar is too back"
        }
        if (closer_side_CDL == "left") {
            return "Bar is too forward"
        }
    }

    if (!is_going_down_CDL) {
        if (hipAngle > highest_hip_angle_CDL) {
            highest_hip_angle_CDL = hipAngle
        }

        if(hipAngle >= 180 - ANGLE_TOLERANCE) {
            is_good_up_CDL = true
            return "Good lockout."
        }

        if (highest_hip_angle_CDL - hipAngle >= 20 + ANGLE_TOLERANCE) {
            is_going_down_CDL = true
            highest_hip_angle_CDL = 0.0
            if (!is_good_up_CDL) {
                return "On next rep, remember to lockout completely."
            }
            is_good_up_CDL = false
        }
    }
    else {
        if (hipAngle < lowest_hip_angle_CDL) {
            lowest_hip_angle_CDL = hipAngle
        }

        if (hipAngle - lowest_hip_angle_CDL >= 20 + ANGLE_TOLERANCE) {
            is_going_down_CDL = false
            lowest_hip_angle_CDL = 360.0
            return "Reset"
        }
    }

    return ""
}