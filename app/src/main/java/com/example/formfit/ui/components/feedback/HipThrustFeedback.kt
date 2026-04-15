package com.example.formfit.ui.components.feedback

import android.graphics.PointF
import com.example.formfit.utils.calculateAngle
import com.example.formfit.utils.determineCloserSide
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

var has_determined_closer_side_HT = false
var closer_side_HT = ""
var is_going_down_HT = false
var is_good_up_HT = false
var has_given_down_feedback_HT = false
var lowest_hip_angle_HT = 360.0
var highest_hip_angle_HT = 0.0
var is_good_foot_placement_HT = false

fun provideHipThrustFeedback(pose: Pose? = null): String {
    if (pose == null) return ""

    if (!has_determined_closer_side_HT) {
        closer_side_HT = determineCloserSide(pose)
        if (closer_side_HT != "error") {
            has_determined_closer_side_HT = true
        }
    }

    if (closer_side_HT == "right") {
        val rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)
        val rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
        val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
        val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        val rightHeel = pose.getPoseLandmark(PoseLandmark.RIGHT_HEEL)
        val rightFoot = pose.getPoseLandmark(PoseLandmark.RIGHT_FOOT_INDEX)

        if (rightAnkle == null || rightKnee == null || rightHip == null || rightShoulder == null ||
            rightHeel == null || rightFoot == null) {
            return ""
        }

        return generateFeedback(rightAnkle, rightKnee, rightHip, rightShoulder, rightHeel, rightFoot)
    }
    else if (closer_side_HT == "left") {
        val leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)
        val leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
        val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val leftHeel = pose.getPoseLandmark(PoseLandmark.LEFT_HEEL)
        val leftFoot = pose.getPoseLandmark(PoseLandmark.LEFT_FOOT_INDEX)

        if (leftAnkle == null || leftKnee == null || leftHip == null || leftShoulder == null ||
            leftHeel == null || leftFoot == null) {
            return ""
        }

        return generateFeedback(leftAnkle, leftKnee, leftHip, leftShoulder, leftHeel, leftFoot)
    }

    return ""
}

private fun generateFeedback(ankle: PoseLandmark, knee: PoseLandmark, hip: PoseLandmark,
                             shoulder: PoseLandmark, heel: PoseLandmark, foot: PoseLandmark): String {
    if (ankle.inFrameLikelihood < 0.95 ||
        knee.inFrameLikelihood < 0.95 ||
        hip.inFrameLikelihood < 0.95 ||
        shoulder.inFrameLikelihood < 0.95) {
        return ""
    }

    val anklePoint = PointF(ankle.position.x, ankle.position.y)
    val kneePoint = PointF(knee.position.x, knee.position.y)
    val hipPoint = PointF(hip.position.x, hip.position.y)
    val shoulderPoint = PointF(shoulder.position.x, shoulder.position.y)
    val heelPoint = PointF(heel.position.x, heel.position.y)
    val footPoint = PointF(foot.position.x, foot.position.y)
    val midFootX = (footPoint.x + heelPoint.x) / 2

    val kneeAngle = calculateAngle(anklePoint, kneePoint, hipPoint)
    val hipAngle = calculateAngle(kneePoint, hipPoint, shoulderPoint)

    if (!is_going_down_HT) {
        if (hipAngle > highest_hip_angle_HT) {
            highest_hip_angle_HT = hipAngle
        }

        if (hipAngle >= 180 - ANGLE_TOLERANCE && !is_good_foot_placement_HT) {
            if (closer_side_HT == "right") {
                if (kneePoint.x > midFootX + X_TOLERANCE) {
                    return "Move feet forward."
                }

                if (kneePoint.x < midFootX - X_TOLERANCE) {
                    return "Move feet back."
                }

                if (kneePoint.x < midFootX + X_TOLERANCE &&
                    kneePoint.x > midFootX - X_TOLERANCE) {
                    is_good_foot_placement_HT = true
                    return "Good foot placement"
                }
            }
            else if (closer_side_HT == "left") {
                if (kneePoint.x > midFootX + X_TOLERANCE) {
                    return "Move feet back."
                }

                if (kneePoint.x < midFootX - X_TOLERANCE) {
                    return "Move feet forward."
                }

                if (kneePoint.x < midFootX + X_TOLERANCE &&
                    kneePoint.x > midFootX - X_TOLERANCE) {
                    is_good_foot_placement_HT = true
                    return "Good foot placement"
                }
            }
        }

        if (kneeAngle >= 90 - ANGLE_TOLERANCE &&
            hipAngle >= 180 - ANGLE_TOLERANCE) {
            is_good_up_HT = true
            return "Good lockout."
        }

        if (highest_hip_angle_HT - hipAngle >= 20 + ANGLE_TOLERANCE) {
            is_going_down_HT = true
            highest_hip_angle_HT = 0.0
            if (!is_good_up_HT) {
                return "On next rep, lift hips higher."
            }
            is_good_up_HT = false
        }
    }
    else {
        if (!has_given_down_feedback_HT) {
            has_given_down_feedback_HT = true
            return "Lower hips as much as possible."
        }

        if (hipAngle < lowest_hip_angle_HT) {
            lowest_hip_angle_HT = hipAngle
        }

        if (hipAngle - lowest_hip_angle_HT >= 20 + ANGLE_TOLERANCE) {
            is_going_down_HT = false
            lowest_hip_angle_HT = 360.0
            has_given_down_feedback_HT = false
            return "Reset"
        }
    }

    return ""
}

fun resetHipThrustVariables() {
    has_determined_closer_side_HT = false
    closer_side_HT = ""
    is_going_down_HT = false
    is_good_up_HT = false
    has_given_down_feedback_HT = false
    lowest_hip_angle_HT = 360.0
    highest_hip_angle_HT = 0.0
    is_good_foot_placement_HT = false

}