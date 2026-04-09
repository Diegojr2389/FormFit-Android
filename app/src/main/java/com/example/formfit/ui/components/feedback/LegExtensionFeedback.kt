package com.example.formfit.ui.components.feedback

import android.graphics.PointF
import com.example.formfit.utils.calculateAngle
import com.example.formfit.utils.determineCloserSide
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

var has_determined_closer_side_leg_extension = false
var closer_side_leg_extension = ""
var is_going_down_leg_extension = false
var highest_leg_angle_leg_extension = 0.0
var lowest_leg_angle_leg_extension = 360.0
var is_good_up_leg_extension = false


fun provideLegExtensionFeedback(pose: Pose? = null): String {
    if (pose == null) return ""

    if (!has_determined_closer_side_leg_extension) {
        closer_side_leg_extension = determineCloserSide(pose)
        if (closer_side_leg_extension != "error"){
            has_determined_closer_side_leg_extension = true
        }
    }

    if (closer_side_leg_extension == "right") {
        val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
        val rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
        val rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)

        if (rightShoulder == null || rightHip == null || rightKnee == null || rightAnkle == null) {
            return ""
        }

        return generateFeedback(rightShoulder, rightHip, rightKnee, rightAnkle)
    }
    else if (closer_side_leg_extension == "left") {
        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        val leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
        val leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)

        if (leftShoulder == null || leftHip == null || leftKnee == null || leftAnkle == null) {
            return ""
        }

        return generateFeedback(leftShoulder, leftHip, leftKnee, leftAnkle)
    }
    return ""
}

private fun generateFeedback(shoulder: PoseLandmark, hip: PoseLandmark, knee: PoseLandmark, ankle: PoseLandmark): String {
    if (shoulder.inFrameLikelihood < 0.95 ||
        hip.inFrameLikelihood < 0.95 ||
        knee.inFrameLikelihood < 0.95 ||
        ankle.inFrameLikelihood < 0.95) {
        return ""
    }

    val shoulderPoint = PointF(shoulder.position.x, shoulder.position.y)
    val hipPoint = PointF(hip.position.x, hip.position.y)
    val kneePoint = PointF(knee.position.x, knee.position.y)
    val anklePoint = PointF(ankle.position.x, ankle.position.y)

    val hipAngle = calculateAngle(shoulderPoint, hipPoint, kneePoint)
    val legAngle = calculateAngle(hipPoint, kneePoint, anklePoint)



//    if (hipAngle < 90 + ANGLE_TOLERANCE) {
//        return "You are leaning too forward."
//    }

    if (!is_going_down_leg_extension) {
        // update highest_leg_angle_leg_extension
        if (legAngle > highest_leg_angle_leg_extension) {
            highest_leg_angle_leg_extension = legAngle
        }
        if (legAngle >= 180 - ANGLE_TOLERANCE) {
            is_good_up_leg_extension = true
            return "Perfect. Keep it controlled."
        }
        // going down
        if (highest_leg_angle_leg_extension - legAngle >= 40 + ANGLE_TOLERANCE) {
            is_going_down_leg_extension = true
            if (!is_good_up_leg_extension) {
                return "On next rep, extend your legs more."
            }
        }
    }
    else {
        if (legAngle < lowest_leg_angle_leg_extension) {
            lowest_leg_angle_leg_extension = legAngle
        }

        // extended legs well
        if (legAngle <= 90 + ANGLE_TOLERANCE) {
            is_going_down_leg_extension = false
            is_good_up_leg_extension = false
            highest_leg_angle_leg_extension = 0.0
            lowest_leg_angle_leg_extension = 360.0
            return "Good. Full range of motion."
        }

        if (legAngle - lowest_leg_angle_leg_extension >= 30 + ANGLE_TOLERANCE) {
            is_going_down_leg_extension = false
            is_good_up_leg_extension = false
            highest_leg_angle_leg_extension = 0.0
            lowest_leg_angle_leg_extension = 360.0
            return "On next rep, extend your legs higher."
        }
    }
    return ""
}