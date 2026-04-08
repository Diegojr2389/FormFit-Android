package com.example.formfit.ui.components.feedback

import android.graphics.PointF
import com.example.formfit.utils.calculateAngle
import com.example.formfit.utils.determineCloserLeg
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

var lowest_angle_squat = 360.0
var closer_leg_squat = "";
var has_determined_closer_leg_squat = false
var medium_squat_reached = false
var deep_squat_reached = false

fun provideSquatFeedback(pose: Pose? = null): String{
    if (pose == null) return ""

    if (!has_determined_closer_leg_squat) {
        closer_leg_squat = determineCloserLeg(pose)
        if (closer_leg_squat != "error") has_determined_closer_leg_squat = true
    }

    if (closer_leg_squat == "right") {
        val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
        val rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
        val rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)

        if (rightHip == null || rightKnee == null || rightAnkle == null) return ""

        else if (rightHip.inFrameLikelihood < 0.95 ||
                 rightKnee.inFrameLikelihood < 0.95 ||
                 rightAnkle.inFrameLikelihood < 0.95) {
            return ""
        }
        else {
            val rightHipPoint = PointF(rightHip.position.x, rightHip.position.y)
            val rightKneePoint = PointF(rightKnee.position.x, rightKnee.position.y)
            val rightAnklePoint = PointF(rightAnkle.position.x, rightAnkle.position.y)

            val currentAngle = calculateAngle(rightHipPoint, rightKneePoint, rightAnklePoint)

            if (lowest_angle_squat > currentAngle) {
                lowest_angle_squat = currentAngle
            }

//            if (!squats_started) return ""

            // Medium/Deep squat is optimal
            if (!deep_squat_reached) {
                // Medium/Deep squat is optimal
                if (lowest_angle_squat <= 90 && lowest_angle_squat > 70) {
                    if (!medium_squat_reached) {
                        medium_squat_reached = true
                        return "Solid depth!"
                    }
                }
                else if (lowest_angle_squat <= 70) {
                    deep_squat_reached = true
                    return "Excellent! You have hit a deep squat!"
                }
            }

            // to reset lowest angle (going up)
            if ((currentAngle - lowest_angle_squat) > 50 && currentAngle > 160) {
                lowest_angle_squat = 360.0
                medium_squat_reached = false
                deep_squat_reached = false
            }
        }
    }

    else if (closer_leg_squat == "left") {
        val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        val leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
        val leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)

        if (leftHip == null || leftKnee == null || leftAnkle == null) return ""
        else if (leftHip.inFrameLikelihood < 0.95 ||
            leftKnee.inFrameLikelihood < 0.95 ||
            leftAnkle.inFrameLikelihood < 0.95) {
            return ""
        }
        else {
            val leftHipPoint = PointF(leftHip.position.x, leftHip.position.y)
            val leftKneePoint = PointF(leftKnee.position.x, leftKnee.position.y)
            val leftAnklePoint = PointF(leftAnkle.position.x, leftAnkle.position.y)

            val currentAngle = calculateAngle(leftHipPoint, leftKneePoint, leftAnklePoint)

            // to calculate lowest angle
            if (lowest_angle_squat > currentAngle) {
                lowest_angle_squat = currentAngle
            }

            // Medium/Deep squat is optimal
            if (!deep_squat_reached) {
                // Medium/Deep squat is optimal
                if (lowest_angle_squat <= 90 && lowest_angle_squat > 70) {
                    if (!medium_squat_reached) {
                        medium_squat_reached = true
                        return "Solid depth!"
                    }
                }
                else if (lowest_angle_squat <= 45) {
                    deep_squat_reached = true
                    return "Excellent! You have hit a deep squat!"
                }
            }

            // to reset lowest angle (going up)
            if ((currentAngle - lowest_angle_squat) > 50 && currentAngle > 160) {
                lowest_angle_squat = 360.0
                medium_squat_reached = false
                deep_squat_reached = false
            }
        }
    }

    return ""
}