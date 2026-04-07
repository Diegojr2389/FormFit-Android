package com.example.formfit.ui.components.feedback

import android.graphics.PointF
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import com.example.formfit.utils.calculateAngle
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

var lowest_angle_barbell_curl = 360.0
var closer_arm_barbell_curl = ""
var has_determined_closer_arm_barbell_curl = false

var going_down_barbell_curl = false
val ANGLE_TOLERANCE = 15.0f

var isGoodBarbellCurlRep = false

fun provideBarbellCurlFeedback(pose: Pose? = null): String {
    if (pose == null) return ""

    if (!has_determined_closer_arm_barbell_curl) {
        closer_arm_barbell_curl = determineCloserArm(pose)
        if (closer_arm_barbell_curl != "error") has_determined_closer_arm_barbell_curl = true
    }

    if(closer_arm_barbell_curl == "right") {
        val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        val rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
        val rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
        val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)

        if (rightShoulder == null || rightElbow == null || rightWrist == null || rightHip == null) return ""

        else if (rightShoulder.inFrameLikelihood < 0.95 ||
                 rightElbow.inFrameLikelihood < 0.95 ||
                 rightWrist.inFrameLikelihood < 0.95) {
            return ""
        }
        else {
            val rightShoulderPoint = PointF(rightShoulder.position.x, rightShoulder.position.y)
            val rightElbowPoint = PointF(rightElbow.position.x, rightElbow.position.y)
            val rightWristPoint = PointF(rightWrist.position.x, rightWrist.position.y)
            val rightHipPoint = PointF(rightHip.position.x, rightHip.position.y)

            val currentArmAngle = calculateAngle(rightShoulderPoint, rightElbowPoint, rightWristPoint)
            val hShElAngle = calculateAngle(rightHipPoint, rightShoulderPoint, rightElbowPoint)

            if (lowest_angle_barbell_curl > currentArmAngle) lowest_angle_barbell_curl = currentArmAngle

            if (hShElAngle > (15 + ANGLE_TOLERANCE)) return "Elbow is too forward"

            // curl should reach about 60 degrees at the top of the rep
            if (currentArmAngle < 60 + ANGLE_TOLERANCE) {
                return "Perfect"
            }

            // to reset lowest angle (going up)
            if ((lowest_angle_barbell_curl > 60 + ANGLE_TOLERANCE) &&
                (currentArmAngle - lowest_angle_barbell_curl) > 50) {
                lowest_angle_barbell_curl = 360.0
                return "On next rep, curl the bar more until you hear perfect."
            }
        }
    }

    else if (closer_arm_barbell_curl == "left") {
        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
        val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
        val leftHip = pose.getPoseLandmark((PoseLandmark.LEFT_HIP))

        if (leftShoulder == null || leftElbow == null || leftWrist == null || leftHip == null) return ""

        else if (leftShoulder.inFrameLikelihood < 0.95 ||
            leftElbow.inFrameLikelihood < 0.95 ||
            leftWrist.inFrameLikelihood < 0.95 ||
            leftHip.inFrameLikelihood < 0.95) {
            return ""
        }
        else {
            val leftShoulderPoint = PointF(leftShoulder.position.x, leftShoulder.position.y)
            val leftElbowPoint = PointF(leftElbow.position.x, leftElbow.position.y)
            val leftWristPoint = PointF(leftWrist.position.x, leftWrist.position.y)
            val leftHipPoint = PointF(leftHip.position.x, leftHip.position.y)

            val currentArmAngle = calculateAngle(leftShoulderPoint, leftElbowPoint, leftWristPoint)
            val hShElAngle = calculateAngle(leftHipPoint, leftShoulderPoint, leftElbowPoint)

            if (hShElAngle > (15 + ANGLE_TOLERANCE)) return "Elbow is too forward";

            // going up
            if (!going_down_barbell_curl) {
                if (lowest_angle_barbell_curl > currentArmAngle) {
                    lowest_angle_barbell_curl = currentArmAngle
                }

                // curl should reach about 60 degrees at the top of the rep
                if (currentArmAngle < 60 + ANGLE_TOLERANCE) {
                    isGoodBarbellCurlRep = true
                    return "Perfect"
                }

                if (currentArmAngle - lowest_angle_barbell_curl > 50) {
                    going_down_barbell_curl = true
                    if (!isGoodBarbellCurlRep) return "On next rep, curl the bar more until you hear perfect."
                }
            }
            else {
                if (currentArmAngle > 150 - ANGLE_TOLERANCE) {
                    lowest_angle_barbell_curl = currentArmAngle
                    going_down_barbell_curl = false
                    isGoodBarbellCurlRep = false
                    Log.d("Feedback", "RESET")
                    return "Reset"
                }
            }
        }
    }
    return ""
}

private fun determineCloserArm(pose: Pose) : String {
    val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
    val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
    val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)

    val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
    val rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
    val rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)

    // can't determine closer arm if landmarks on either arm are not visible
    if (leftShoulder == null && leftElbow == null && leftWrist == null &&
        rightShoulder == null && rightElbow == null && rightWrist == null) {
        return "error"
    }

    // right arm is closer if any landmark on the left arm is null
    if (leftShoulder == null || leftElbow == null || leftWrist == null) return "right"

    // left arm is closer if any landmark on the right arm is null
    if (rightShoulder == null || rightElbow == null || rightWrist == null) return "left"

    // if z values of left arm landmarks are greater than the z values of the right arm landmarks,
    // then the right arm is closer to the screen
    if ((leftShoulder.position3D.z + leftElbow.position3D.z + leftWrist.position3D.z) >
        (rightShoulder.position3D.z + rightElbow.position3D.z + rightWrist.position3D.z)) {
        return "right"
    }

    // if z values of left arm landmarks are less than the z values of the right arm landmarks,
    // then the left arm is closer to the screen
    if ((leftShoulder.position3D.z + leftElbow.position3D.z + leftWrist.position3D.z) <
        (rightShoulder.position3D.z + rightElbow.position3D.z + rightWrist.position3D.z)) {
        return "left"
    }

    // if nothing has been returned, then an error has occurred, so we return "error"
    return "error"
}