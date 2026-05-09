package com.example.formfit.ui.components.feedback

import android.graphics.PointF
import com.example.formfit.models.FormFeedback
import com.example.formfit.utils.calculateAngle
import com.example.formfit.utils.determineCloserSide
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

var has_determined_closer_side_HLR = false
var closer_side_HLR = ""
var is_going_down_HLR = false
var is_good_up_HLR = false
var lowest_knee_Y_HLR = 3000.0f
var highest_knee_Y_HLR = 0.0f
var are_knees_bent_well = false

fun provideHangingLegRaiseFeedback(pose: Pose? = null): FormFeedback {
    if (pose == null) return FormFeedback("")

    if (!has_determined_closer_side_HLR) {
        closer_side_HLR = determineCloserSide(pose)
        if (closer_side_HLR != "error") {
            has_determined_closer_side_HLR = true
        }
    }

    if (closer_side_HLR == "right") {
        val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
        val rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
        val rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)

        if (rightShoulder == null || rightHip == null || rightKnee == null || rightAnkle == null) {
            return FormFeedback("")
        }

        return generateFeedback(rightShoulder, rightHip, rightKnee, rightAnkle)
    }
    else if (closer_side_HLR == "left") {
        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        val leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
        val leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)

        if (leftShoulder == null || leftHip == null || leftKnee == null || leftAnkle == null) {
            return FormFeedback("")
        }

        return generateFeedback(leftShoulder, leftHip, leftKnee, leftAnkle)
    }
    return FormFeedback("")
}

private fun generateFeedback(shoulder: PoseLandmark, hip: PoseLandmark, knee: PoseLandmark,
                             ankle: PoseLandmark): FormFeedback {
    if (shoulder.inFrameLikelihood < 0.95 ||
        hip.inFrameLikelihood < 0.95 ||
        knee.inFrameLikelihood < 0.95 ||
        ankle.inFrameLikelihood < 0.95) {
        return FormFeedback("")
    }

    val shoulderPoint = PointF(shoulder.position.x, shoulder.position.y)
    val hipPoint = PointF(hip.position.x, hip.position.y)
    val kneePoint = PointF(knee.position.x, knee.position.y)
    val anklePoint = PointF(ankle.position.x, ankle.position.y)

    val kneeAngle = calculateAngle(hipPoint, kneePoint, anklePoint)

    if (are_knees_bent_well && kneeAngle < 90 + ANGLE_TOLERANCE) {
        are_knees_bent_well = false
        return FormFeedback(
            message = "Knees are too bent.",
            isBadFeedback = true
        )
    }

    if (!are_knees_bent_well && kneeAngle >= 90 + ANGLE_TOLERANCE) {
        are_knees_bent_well = true
        return FormFeedback(
            message = "Perfect. Knees are extended more than 90 degrees",
            isGoodFeedback = true
        )
    }

    if (!is_going_down_HLR) {
        if (kneePoint.y < lowest_knee_Y_HLR) {
            lowest_knee_Y_HLR = kneePoint.y
        }

        if (kneePoint.y <= shoulderPoint.y + Y_TOLERANCE) {
            is_good_up_HLR = true
            return FormFeedback(
                message = "Perfect. Knees have reached shoulder height.",
                isTop = true
            )
        }

        // going down
        if (kneePoint.y - lowest_knee_Y_HLR >= 30 + Y_TOLERANCE) {
            is_going_down_HLR = true
            lowest_knee_Y_HLR = 3000.0f
            if (!is_good_up_HLR) {
                return FormFeedback(
                    message = "On next rep, have knees reach shoulder height",
                    isNextRepFeedback = true
                )
            }
            is_good_up_HLR = false
        }
    }
    else {
        if (kneePoint.y > highest_knee_Y_HLR) {
            highest_knee_Y_HLR = kneePoint.y
        }

        if (highest_knee_Y_HLR - kneePoint.y >= 30 + Y_TOLERANCE) {
            is_going_down_HLR = false
            highest_knee_Y_HLR = 0.0f
            return FormFeedback("Reset")
        }
    }

    return FormFeedback("")
}
fun resetHLRVariables() {
    has_determined_closer_side_HLR = false
    closer_side_HLR = ""
    is_going_down_HLR = false
    is_good_up_HLR = false
    lowest_knee_Y_HLR = 3000.0f
    highest_knee_Y_HLR = 0.0f

}