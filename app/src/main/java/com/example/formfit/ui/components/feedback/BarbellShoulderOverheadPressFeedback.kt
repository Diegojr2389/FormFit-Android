package com.example.formfit.ui.components.feedback

import android.graphics.PointF
import android.util.Log
import com.example.formfit.utils.calculateAngle
import com.example.formfit.utils.determineCloserSide
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

var has_determined_closer_side_SOP = false
var closer_side_SOP = ""
var is_going_down_SOP = false
var highest_wrist_Y = 0.0f
var highest_elbow_angle = 0.0
var is_good_up_SOP = false
var is_good_down_SOP = false
var is_grip_good_SOP = false
var x_offset_SOP = 0
var Y_TOLERANCE = 15

fun provideBarbellOverheadPressFeedback(pose: Pose? = null): String {
    if (pose == null) return ""

    if (!has_determined_closer_side_SOP) {
        closer_side_SOP = determineCloserSide(pose)
        if (closer_side_SOP != "error") {
            has_determined_closer_side_SOP = true
        }
    }

    if (closer_side_SOP == "right") {
        x_offset_SOP = 100
        val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
        val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        val rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
        val rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)

        if (rightHip == null || rightShoulder == null || rightElbow == null || rightWrist == null) {
            return ""
        }

        return generateFeedback(rightHip, rightShoulder, rightElbow, rightWrist)
    }
    else if (closer_side_SOP == "left") {
        x_offset_SOP = -100
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

    // should be 180 when wrist crosses shoulder
    val hipShElAngle = calculateAngle(hipPoint, shoulderPoint, elbowPoint)
    val elbowAngle = calculateAngle(shoulderPoint, elbowPoint, wristPoint)
    // checks if the grip is good
    if (!is_grip_good_SOP) {
        if (hipShElAngle >= 45 - ANGLE_TOLERANCE && hipShElAngle <= 45 + ANGLE_TOLERANCE) {
            is_grip_good_SOP = true
            return "Good grip width. You may now start."
        }
        else if (hipShElAngle <= 45 - ANGLE_TOLERANCE) {
            return "Widen your grip until you hear: Good grip width"
        }
        else if (hipShElAngle >= 45 + ANGLE_TOLERANCE) {
            return "Decrease width of your grip until you hear: Good grip width"
        }
    }
    // checks if bar is going low and high enough
    else {
        if (!is_going_down_SOP) {
            if (elbowAngle > highest_elbow_angle) {
                highest_elbow_angle = elbowAngle
            }

            // good rep at top
            if (elbowAngle >= 180 - ANGLE_TOLERANCE) {
                is_good_up_SOP = true
                return "Perfect. Arms extended all the way."
            }

            if (highest_elbow_angle - elbowAngle >= 20 + ANGLE_TOLERANCE) {
                is_going_down_SOP = true
                highest_elbow_angle = 0.0
                if (!is_good_up_SOP) {
                    return "On next rep, extend your arms more"
                }
                is_good_up_SOP = false
            }
        }
        else {
            if (wristPoint.y > highest_wrist_Y) {
                highest_wrist_Y = wristPoint.y
            }

            if (wristPoint.y >= shoulderPoint.y - Y_TOLERANCE) {
                is_good_down_SOP = true
                return "Perfect. Bar and shoulders are aligned."
            }

            if (highest_wrist_Y - wristPoint.y >= 20 + Y_TOLERANCE) {
                is_going_down_SOP = false
                highest_wrist_Y = 0.0f
                if (!is_good_down_SOP) {
                    return "On next rep, lower bar to shoulder level."
                }
                is_good_down_SOP = false
            }
        }
    }


    return ""
}

fun resetBSOPVariables() {
    has_determined_closer_side_SOP = false
    closer_side_SOP = ""
    is_going_down_SOP = false
    highest_wrist_Y = 0.0f
    highest_elbow_angle = 0.0
    is_good_up_SOP = false
    is_good_down_SOP = false
    is_grip_good_SOP = false
    x_offset_SOP = 0
    Y_TOLERANCE = 15
}