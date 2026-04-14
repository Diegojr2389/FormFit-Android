package com.example.formfit.ui.components.feedback

import android.graphics.PointF
import com.example.formfit.utils.calculateAngle
import com.example.formfit.utils.determineCloserArm
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

var has_determined_closer_arm_dip = false
var closer_arm_dip = ""
var is_going_down_dip = true
var is_good_down_dip = false
var is_good_up_dip = false
var lowest_elbow_angle_dip = 360.0
var highest_elbow_angle_dip = 0.0
var is_elbow_and_wrist_aligned_dip = false

fun provideDipFeedback(pose: Pose? = null): String {
    if (pose == null) return ""

    if (!has_determined_closer_arm_dip) {
        closer_arm_dip = determineCloserArm(pose)
        if (closer_arm_dip != "error") {
         has_determined_closer_arm_dip = true
        }
    }

    if (closer_arm_dip == "right") {
        val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        val rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
        val rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)

        if (rightShoulder == null || rightElbow == null || rightWrist == null) return ""

        return generateFeedback(rightShoulder, rightElbow, rightWrist)
    }
    else if (closer_arm_dip == "left") {
        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
        val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)

        if (leftShoulder == null || leftElbow == null || leftWrist == null) return ""

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

    val elbowAngle  = calculateAngle(shoulderPoint, elbowPoint, wristPoint)

    if (!is_elbow_and_wrist_aligned_dip &&
        elbowPoint.x > wristPoint.x - X_TOLERANCE &&
        elbowPoint.x < wristPoint.x + X_TOLERANCE) {
        is_elbow_and_wrist_aligned_dip = true
        return "Good. Wrists and elbows are aligned."
    }

    if (is_elbow_and_wrist_aligned_dip &&
        elbowPoint.x < wristPoint.x - X_TOLERANCE ) {
        if (closer_arm_dip == "right") {
            is_elbow_and_wrist_aligned_dip = false
            return "Elbow is too back. Align vertically with wrist."
        }
        if (closer_arm_dip == "left") {
            is_elbow_and_wrist_aligned_dip = false
            return "Elbow is too forward. Align vertically with wrist."
        }
    }

    if (is_elbow_and_wrist_aligned_dip &&
        elbowPoint.x > wristPoint.x + X_TOLERANCE ) {
        if (closer_arm_dip == "right") {
            is_elbow_and_wrist_aligned_dip = false
            return "Elbow is too forward. Align vertically with wrist."
        }
        if (closer_arm_dip == "left") {
            is_elbow_and_wrist_aligned_dip = false
            return "Elbow is too back. Align vertically with wrist."
        }
    }

    if (!is_going_down_dip) {
        if (elbowAngle > highest_elbow_angle_dip) {
            highest_elbow_angle_dip = elbowAngle
        }

        if (elbowAngle >= 180 - ANGLE_TOLERANCE) {
            is_good_up_dip = true
            return "Strong lockout. Arms fully extended."
        }

        if (highest_elbow_angle_dip - elbowAngle >= 20 + ANGLE_TOLERANCE) {
            is_going_down_dip = true
            highest_elbow_angle_dip = 0.0
            if (!is_good_up_dip) {
                return "On next rep, fully extend your arms on lockout."
            }
            is_good_up_dip = false
        }
    }
    else {
        if (elbowAngle < lowest_elbow_angle_dip) {
            lowest_elbow_angle_dip = elbowAngle
        }

        if (elbowAngle < 90 + ANGLE_TOLERANCE) {
            is_good_down_dip = true
            return "Good depth. Elbows passed 90 degrees."
        }

        if (elbowAngle - lowest_elbow_angle_dip >= 20 + ANGLE_TOLERANCE) {
            is_going_down_dip = false
            lowest_elbow_angle_dip = 360.0
            if (!is_good_down_dip) {
                return "On next rep, lower your body more."
            }
            is_good_down_dip = false
        }
    }

    return ""
}

fun resetDipVariables() {
    has_determined_closer_arm_dip = false
    closer_arm_dip = ""
    is_going_down_dip = true
    is_good_down_dip = false
    is_good_up_dip = false
    lowest_elbow_angle_dip = 360.0
    highest_elbow_angle_dip = 0.0
    is_elbow_and_wrist_aligned_dip = false

}