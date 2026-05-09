package com.example.formfit.ui.components.feedback

import android.graphics.PointF
import com.example.formfit.models.FormFeedback
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

fun provideDipFeedback(pose: Pose? = null): FormFeedback {
    if (pose == null) return FormFeedback("")

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

        if (rightShoulder == null || rightElbow == null || rightWrist == null) {
            return FormFeedback("")
        }

        return generateFeedback(rightShoulder, rightElbow, rightWrist)
    }
    else if (closer_arm_dip == "left") {
        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
        val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)

        if (leftShoulder == null || leftElbow == null || leftWrist == null) {
            return FormFeedback("")
        }

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

    val elbowAngle  = calculateAngle(shoulderPoint, elbowPoint, wristPoint)

    if (!is_elbow_and_wrist_aligned_dip &&
        elbowPoint.x > wristPoint.x - X_TOLERANCE &&
        elbowPoint.x < wristPoint.x + X_TOLERANCE) {
        is_elbow_and_wrist_aligned_dip = true
        return FormFeedback(
            message = "Good. Wrists and elbows are aligned.",
            isGoodFeedback = true
        )
    }

    if (is_elbow_and_wrist_aligned_dip &&
        elbowPoint.x < wristPoint.x - X_TOLERANCE ) {
        if (closer_arm_dip == "right") {
            is_elbow_and_wrist_aligned_dip = false
            return FormFeedback(
                message = "Elbow is too back. Align vertically with wrist.",
                isBadFeedback = true
            )
        }
        if (closer_arm_dip == "left") {
            is_elbow_and_wrist_aligned_dip = false
            return FormFeedback(
                message = "Elbow is too forward. Align vertically with wrist.",
                isBadFeedback = true
            )
        }
    }

    if (is_elbow_and_wrist_aligned_dip &&
        elbowPoint.x > wristPoint.x + X_TOLERANCE ) {
        if (closer_arm_dip == "right") {
            is_elbow_and_wrist_aligned_dip = false
            return FormFeedback(
                message = "Elbow is too forward. Align vertically with wrist.",
                isBadFeedback = true
            )
        }
        if (closer_arm_dip == "left") {
            is_elbow_and_wrist_aligned_dip = false
            return FormFeedback(
                message = "Elbow is too back. Align vertically with wrist.",
                isBadFeedback = true
            )
        }
    }

    if (!is_going_down_dip) {
        if (elbowAngle > highest_elbow_angle_dip) {
            highest_elbow_angle_dip = elbowAngle
        }

        if (elbowAngle >= 180 - ANGLE_TOLERANCE) {
            is_good_up_dip = true
            return FormFeedback(
                message = "Strong lockout. Arms fully extended.",
                isTop = true
            )
        }

        if (highest_elbow_angle_dip - elbowAngle >= 20 + ANGLE_TOLERANCE) {
            is_going_down_dip = true
            highest_elbow_angle_dip = 0.0
            if (!is_good_up_dip) {
                return FormFeedback(
                    message = "On next rep, fully extend your arms on lockout.",
                    isNextRepFeedback = true
                )
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
            return FormFeedback(
                message = "Good depth. Elbows passed 90 degrees.",
                isBottom = true
            )
        }

        if (elbowAngle - lowest_elbow_angle_dip >= 20 + ANGLE_TOLERANCE) {
            is_going_down_dip = false
            lowest_elbow_angle_dip = 360.0
            if (!is_good_down_dip) {
                return FormFeedback(
                    message = "On next rep, lower your body more.",
                    isNextRepFeedback = true
                )
            }
            is_good_down_dip = false
        }
    }

    return FormFeedback("")
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