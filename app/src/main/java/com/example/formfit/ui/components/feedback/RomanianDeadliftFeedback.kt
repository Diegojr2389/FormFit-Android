package com.example.formfit.ui.components.feedback

import android.graphics.PointF
import com.example.formfit.utils.calculateAngle
import com.example.formfit.utils.determineCloserSide
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

var has_determined_closer_side_RDL = false
var closer_side_RDL = ""
var is_going_down_RDL = false
var is_good_up_RDL = false
var highest_hip_angle_RDL = 0.0
var lowest_hip_angle_RDL = 360.0
var is_bar_aligned_with_midfoot_RDL = false
var is_good_knee_angle_RDL = true

fun provideRomanianDeadliftFeedback(pose: Pose? = null): String {
    if (pose == null) return ""
    if (!has_determined_closer_side_RDL) {
        closer_side_RDL = determineCloserSide(pose)
        if (closer_side_RDL != "error") {
            has_determined_closer_side_RDL = true
        }

    }

    if (closer_side_RDL == "right") {
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
    else if (closer_side_RDL == "left") {
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
    val kneeAngle  = calculateAngle(heelPoint, kneePoint, hipPoint)

    if (!is_bar_aligned_with_midfoot_RDL &&
        wristPoint.x <= midFootX + X_TOLERANCE &&
        wristPoint.x >= midFootX - X_TOLERANCE) {
        is_bar_aligned_with_midfoot_RDL = true
        return "Good bar and midfoot alignment"
    }

    if (is_bar_aligned_with_midfoot_RDL &&
        wristPoint.x >= midFootX + X_TOLERANCE) {
        is_bar_aligned_with_midfoot_RDL = false
        if (closer_side_RDL == "right") {
            return "Bar is too forward"
        }
        if (closer_side_RDL == "left") {
            return "Bar is too back"
        }
    }

    if (is_bar_aligned_with_midfoot_RDL &&
        wristPoint.x < midFootX - X_TOLERANCE) {
        is_bar_aligned_with_midfoot_RDL = false
        if (closer_side_RDL == "right") {
            return "Bar is too back"
        }
        if (closer_side_RDL == "left") {
            return "Bar is too forward"
        }
    }

    if (is_good_knee_angle_RDL && kneeAngle <= 150 - ANGLE_TOLERANCE) {
        is_good_knee_angle_RDL = false
        return "Knees are too bent."
    }

    if (!is_good_knee_angle_RDL && kneeAngle >= 150 - ANGLE_TOLERANCE) {
        is_good_knee_angle_RDL = true
        return "Good knee bend."
    }

    if (!is_going_down_RDL) {
        if (hipAngle > highest_hip_angle_RDL) {
            highest_hip_angle_RDL = hipAngle
        }

        if(hipAngle >= 180 - ANGLE_TOLERANCE) {
            is_good_up_RDL = true
            return "Good lockout."
        }

        if (highest_hip_angle_RDL - hipAngle >= 20 + ANGLE_TOLERANCE) {
            is_going_down_RDL = true
            highest_hip_angle_RDL = 0.0
            if (!is_good_up_RDL) {
                return "On next rep, remember to lockout completely."
            }
            is_good_up_RDL = false
        }
    }
    else {
        if (hipAngle < lowest_hip_angle_RDL) {
            lowest_hip_angle_RDL = hipAngle
        }

        if (hipAngle - lowest_hip_angle_RDL >= 20 + ANGLE_TOLERANCE) {
            is_going_down_RDL = false
            lowest_hip_angle_RDL = 360.0
            return "Reset"
        }
    }

    return ""
}

fun resetRDLVariables() {
    has_determined_closer_side_RDL = false
    closer_side_RDL = ""
    is_going_down_RDL = false
    is_good_up_RDL = false
    highest_hip_angle_RDL = 0.0
    lowest_hip_angle_RDL = 360.0
    is_bar_aligned_with_midfoot_RDL = false
    is_good_knee_angle_RDL = true
    X_TOLERANCE = 20
}