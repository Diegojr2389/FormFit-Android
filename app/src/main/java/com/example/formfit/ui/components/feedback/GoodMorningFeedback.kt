package com.example.formfit.ui.components.feedback

import android.graphics.PointF
import com.example.formfit.utils.calculateAngle
import com.example.formfit.utils.determineCloserSide
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

var has_determined_closer_side_GM = false
var closer_side_GM = ""
var is_going_down_GM = true
var lowest_hip_angle_GM = 360.0
var highest_hip_angle_GM = 0.0
var is_good_up_GM = false
var is_good_down_GM = false

fun provideGoodMorningFeedback(pose: Pose? = null) : String {
    if (pose == null) return ""

    if (!has_determined_closer_side_GM) {
        closer_side_GM = determineCloserSide(pose)
        if (closer_side_GM != "error") {
            has_determined_closer_side_GM = true
        }
    }

    if (closer_side_GM == "right") {
        val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
        val rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
        val rightFoot = pose.getPoseLandmark(PoseLandmark.RIGHT_FOOT_INDEX)
        val rightHeel = pose.getPoseLandmark(PoseLandmark.RIGHT_HEEL)

        if (rightShoulder == null || rightHip == null || rightKnee == null ||
            rightFoot == null || rightHeel == null) {
            return ""
        }

        return generateFeedback(rightShoulder, rightHip, rightKnee, rightHeel, rightFoot)
    }
    else if (closer_side_GM == "left") {
        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        val leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
        val leftFoot = pose.getPoseLandmark(PoseLandmark.LEFT_FOOT_INDEX)
        val leftHeel = pose.getPoseLandmark(PoseLandmark.LEFT_HEEL)

        if (leftShoulder == null || leftHip == null || leftKnee == null ||
            leftFoot == null || leftHeel == null) {
            return ""
        }

        return generateFeedback(leftShoulder, leftHip, leftKnee, leftHeel, leftFoot)
    }
    return ""
}

private fun generateFeedback(shoulder: PoseLandmark, hip: PoseLandmark, knee: PoseLandmark,
                             heel: PoseLandmark, foot: PoseLandmark): String {
    if (shoulder.inFrameLikelihood < 0.95 ||
        hip.inFrameLikelihood < 0.95 ||
        knee.inFrameLikelihood < 0.95 ||
        heel.inFrameLikelihood < 0.95 ||
        foot.inFrameLikelihood < 0.95) {
        return ""
    }

    val shoulderPoint = PointF(shoulder.position.x, shoulder.position.y)
    val hipPoint = PointF(hip.position.x, hip.position.y)
    val kneePoint = PointF(knee.position.x, knee.position.y)
    val midFootX = (foot.position.x + heel.position.x) / 2
    val midFootPoint = PointF(midFootX, foot.position.y)
    // will use point to determine if knee and midfoot are aligned
    val kneeDummyPoint = PointF(midFootX, knee.position.y + 100)
    // will use to determine if hip and shoulders are aligned vertically
    // point is aligned vertically with midfoot and horizontally with hip point
    val hipDummyPoint = PointF(midFootX, hip.position.y)

    val kneeAngle = calculateAngle(midFootPoint, kneePoint, kneeDummyPoint)
    val hipAngle = calculateAngle(midFootPoint, hipDummyPoint, shoulderPoint)

    if (!is_going_down_GM) {
        if (hipAngle > highest_hip_angle_GM) {
            highest_hip_angle_GM = hipAngle
        }

        // good rep at the top (standing neutrally)
        if (hipAngle >= 180 - ANGLE_TOLERANCE) {
            is_good_up_GM = true
            return "Stand tall and squeeze your glutes."
        }

        // going down
        if (highest_hip_angle_GM - hipAngle >= 20 + ANGLE_TOLERANCE) {
            is_going_down_GM = true
            highest_hip_angle_GM = 0.0
            if (!is_good_up_GM) {
                return "On next rep, stand all the way up."
            }
            is_good_up_GM = false
        }
    }
    else {
        if (hipAngle < lowest_hip_angle_GM) {
            lowest_hip_angle_GM = hipAngle
        }

        // good rep at bottom (hip and shoulders are aligned)
        if (hipAngle <= 90 + ANGLE_TOLERANCE) {
            is_good_down_GM = true
            return "Great hinge! Keep it controlled."
        }

        // going up
        if (hipAngle - lowest_hip_angle_GM >= 20 + ANGLE_TOLERANCE) {
            is_going_down_GM = false
            lowest_hip_angle_GM = 360.0
            if (!is_good_down_GM) {
                return "On next rep, lower your torso more"
            }
            is_good_down_GM = false
        }
    }
    return ""
}