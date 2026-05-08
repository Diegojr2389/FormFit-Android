package com.example.formfit.ui.components.feedback

import android.graphics.PointF
import com.example.formfit.models.FormFeedback
import com.example.formfit.utils.calculateAngle
import com.example.formfit.utils.determineCloserLeg
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

var lowest_knee_angle_squat = 360.0
var closer_leg_squat = "";
var has_determined_closer_leg_squat = false
var medium_squat_reached = false
var deep_squat_reached = false
var is_going_down_squat = false



fun provideSquatFeedback(pose: Pose? = null): FormFeedback{
    if (pose == null) return FormFeedback("")

    if (!has_determined_closer_leg_squat) {
        closer_leg_squat = determineCloserLeg(pose)
        if (closer_leg_squat != "error") {
            has_determined_closer_leg_squat = true
        }
    }

    if (closer_leg_squat == "right") {
        val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
        val rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
        val rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)

        if (rightHip == null || rightKnee == null || rightAnkle == null) return FormFeedback("")

        return generateFeedback(rightHip, rightKnee, rightAnkle)
    }

    else if (closer_leg_squat == "left") {
        val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        val leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
        val leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)

        if (leftHip == null || leftKnee == null || leftAnkle == null) return FormFeedback("")

        return generateFeedback(leftHip, leftKnee, leftAnkle)
    }

    return FormFeedback("")
}

private fun generateFeedback(hip: PoseLandmark, knee: PoseLandmark, ankle: PoseLandmark): FormFeedback {
    if (hip.inFrameLikelihood < 0.95 ||
        knee.inFrameLikelihood < 0.95 ||
        ankle.inFrameLikelihood < 0.95) {
        return FormFeedback("")
    }

    val hipPoint = PointF(hip.position.x, hip.position.y)
    val kneePoint = PointF(knee.position.x, knee.position.y)
    val anklePoint = PointF(ankle.position.x, ankle.position.y)

    val kneeAngle = calculateAngle(hipPoint, kneePoint, anklePoint)

    if (is_going_down_squat) {
        // to calculate lowest angle
        if (lowest_knee_angle_squat > kneeAngle) {
            lowest_knee_angle_squat = kneeAngle
        }

        // Medium/Deep squat is optimal
        if (!deep_squat_reached) {
            // Medium/Deep squat is optimal
            if (lowest_knee_angle_squat <= 90 && lowest_knee_angle_squat > 70) {
                if (!medium_squat_reached) {
                    medium_squat_reached = true
                    return FormFeedback(
                        message = "Solid depth!",
                        isGoodFeedback = true
                    )
                }
            }
            else if (lowest_knee_angle_squat <= 45) {
                deep_squat_reached = true
                return FormFeedback(
                    message = "Excellent! You have hit a deep squat!",
                    isGoodFeedback = true
                )
            }
        }
    }
    else {
        // to reset lowest angle (going up)
        if ((kneeAngle - lowest_knee_angle_squat) > 45 + ANGLE_TOLERANCE) {
            lowest_knee_angle_squat = 360.0
            medium_squat_reached = false
            deep_squat_reached = false
            return FormFeedback("Reset")
        }
    }

    return FormFeedback("")
}

fun resetSquatVariables() {
    lowest_knee_angle_squat = 360.0
    closer_leg_squat = "";
    has_determined_closer_leg_squat = false
    medium_squat_reached = false
    deep_squat_reached = false
}