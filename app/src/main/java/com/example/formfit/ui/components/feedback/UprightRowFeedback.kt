package com.example.formfit.ui.components.feedback

import android.graphics.PointF
import com.example.formfit.utils.determineCloserSide
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

var has_determined_closer_side_UR = false
var closer_side_UR = ""
var is_going_down_UR = true
var is_good_bar_path_UR = true
var is_good_up_UR = false
var lowest_Y_elbow_UR = 3000.0f
var highest_Y_elbow_UR = 0.0f

fun provideUprightRowFeedback(pose: Pose? = null): String {
    if (pose == null) return ""

    if (!has_determined_closer_side_UR) {
        closer_side_UR = determineCloserSide(pose)
        if (closer_side_UR != "error") {
            has_determined_closer_side_UR = true
        }
    }

    if (closer_side_UR == "right") {
        val rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
        val rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
        val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
        val rightFoot = pose.getPoseLandmark(PoseLandmark.RIGHT_FOOT_INDEX)

        if (rightWrist == null || rightElbow == null || rightShoulder == null || rightHip == null ||
            rightFoot == null) {
            return ""
        }

        return generateFeedback(rightWrist, rightElbow, rightShoulder, rightHip, rightFoot)
    }
    else if (closer_side_UR == "left") {
        val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
        val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        val leftFoot = pose.getPoseLandmark(PoseLandmark.LEFT_FOOT_INDEX)

        if (leftWrist == null || leftElbow == null || leftShoulder == null || leftHip == null ||
            leftFoot == null) {
            return ""
        }

        return generateFeedback(leftWrist, leftElbow, leftShoulder, leftHip, leftFoot)
    }

    return ""
}

private fun generateFeedback(wrist: PoseLandmark, elbow: PoseLandmark, shoulder: PoseLandmark,
                             hip: PoseLandmark, foot: PoseLandmark): String {
    if (wrist.inFrameLikelihood < 0.95 ||
        elbow.inFrameLikelihood < 0.95 ||
        shoulder.inFrameLikelihood < 0.95 ||
        hip.inFrameLikelihood < 0.95 ||
        foot.inFrameLikelihood < 0.95) {
        return ""
    }

    val wristPoint = PointF(wrist.position.x, wrist.position.y)
    val elbowPoint = PointF(elbow.position.x, elbow.position.y)
    val shoulderPoint = PointF(shoulder.position.x, shoulder.position.y)
    val hipPoint = PointF(hip.position.x, hip.position.y)
    val footPoint = PointF(foot.position.x, foot.position.y)
    // RIGHT SIDE
    // Good - wrist.x < foot.x
    // Bad - wrist.x > foot.x

    // LEFT SIDE
    // Good - wrist.x > foot.x
    // Bad - wrist.x < foot.x
    if (wristPoint.x >= footPoint.x + X_TOLERANCE) {
        if (is_good_bar_path_UR && closer_side_UR == "right") {
            is_good_bar_path_UR = false
            return "Bar is too forward"
        }
        if (!is_good_bar_path_UR && closer_side_UR == "left") {
            is_good_bar_path_UR = true
            return "Good bar path."
        }
    }

    if (wristPoint.x < footPoint.x) {
        if (!is_good_bar_path_UR && closer_side_UR == "right") {
            is_good_bar_path_UR = true
            return "Good bar path"
        }
        if (is_good_bar_path_UR && closer_side_UR == "left") {
            is_good_bar_path_UR = false
            return "Bar is too forward"
        }
    }

    if (!is_going_down_UR) {
        if (elbowPoint.y < lowest_Y_elbow_UR) {
            lowest_Y_elbow_UR = elbowPoint.y
        }

        if (elbowPoint.y <= shoulderPoint.y - Y_TOLERANCE) {
            is_good_up_UR = true
            return "Perfect. Elbows reached shoulder height."
        }

        if (elbowPoint.y - lowest_Y_elbow_UR >= 30 + Y_TOLERANCE) {
            is_going_down_UR = true
            lowest_Y_elbow_UR = 3000.0f
            if (!is_good_up_UR) {
                return "On next rep, lift elbows up to shoulder height"
            }
            is_good_up_UR = false
        }
    }
    else {
        if (elbowPoint.y > highest_Y_elbow_UR) {
            highest_Y_elbow_UR = elbowPoint.y
        }

        if (highest_Y_elbow_UR - elbowPoint.y >= 30 + Y_TOLERANCE) {
            is_going_down_UR = false
            highest_Y_elbow_UR = 0.0f
            return "Reset"
        }
    }

    return ""
}
fun resetURVariables() {
    has_determined_closer_side_UR = false
    closer_side_UR = ""
    is_going_down_UR = true
    is_good_bar_path_UR = true
    is_good_up_UR = false
    lowest_Y_elbow_UR = 3000.0f
    highest_Y_elbow_UR = 0.0f
}