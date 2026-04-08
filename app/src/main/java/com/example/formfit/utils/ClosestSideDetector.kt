package com.example.formfit.utils

import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

// determines which arm is closer to the screen to provide better feedback
// can provide better feedback on arm that has higher visibility
fun determineCloserArm(pose: Pose) : String {
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

// determines which leg is closer to the screen to provide better feedback
// can provide better feedback on leg that has higher visibility
fun determineCloserLeg(pose: Pose) : String {
    val leftHip =  pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
    val leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
    val leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)

    val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
    val rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
    val rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)

    // can't determine closer leg if landmarks on either leg are not visible
    if (leftHip == null && leftKnee == null && leftAnkle == null &&
        rightHip == null && rightKnee == null && rightAnkle == null) {
        return "error"
    }

    // right leg is closer if any landmark on the left leg is null
    if (leftHip == null || leftKnee == null || leftAnkle == null) return "right"

    // left leg is closer if any landmark on the right leg is null
    if (rightHip == null || rightKnee == null || rightAnkle == null) return "left"

    // if z values of left leg landmarks are greater than the z values of the right leg landmarks,
    // then the right leg is closer to the screen
    if ((leftHip.position3D.z + leftKnee.position3D.z + leftAnkle.position3D.z) >
        (rightHip.position3D.z + rightKnee.position3D.z + rightAnkle.position3D.z)) {
        return "right"
    }

    // if z values of left leg landmarks are less than the z values of the right leg landmarks,
    // then the left leg is closer to the screen
    if ((leftHip.position3D.z + leftKnee.position3D.z + leftAnkle.position3D.z) <
        (rightHip.position3D.z + rightKnee.position3D.z + rightAnkle.position3D.z)) {
        return "left"
    }

    // if nothing has been returned, then an error has occurred, so we return "error"
    return "error"
}

fun determineCloserSide(pose: Pose) : String {
    val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
    val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
    val leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
    val leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)

    val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
    val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
    val rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
    val rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)

    // can't determine closer side if landmarks on either side are not visible
    if (leftShoulder == null && leftHip == null && leftKnee == null && leftAnkle == null &&
        rightShoulder == null && rightHip == null && rightKnee == null && rightAnkle == null) {
        return "error"
    }

    // right side is closer if any landmark on the left side is null
    if (leftShoulder == null || leftHip == null || leftKnee == null || leftAnkle == null) return "right"

    // left side is closer if any landmark on the right side is null
    if (rightShoulder == null || rightHip == null || rightKnee == null || rightAnkle == null) return "left"

    // if z values of left side landmarks are greater than the z values of the right side landmarks,
    // then the right side is closer to the screen
    if ((leftShoulder.position3D.z + leftHip.position3D.z + leftKnee.position3D.z + leftAnkle.position3D.z) >
        (rightShoulder.position3D.z + rightHip.position3D.z + rightKnee.position3D.z + rightAnkle.position3D.z)) {
        return "right"
    }

    // if z values of left side landmarks are less than the z values of the right side landmarks,
    // then the left side is closer to the screen
    if ((leftShoulder.position3D.z + leftHip.position3D.z + leftKnee.position3D.z + leftAnkle.position3D.z) <
        (rightShoulder.position3D.z + rightHip.position3D.z + rightKnee.position3D.z + rightAnkle.position3D.z)) {
        return "left"
    }

    // if nothing has been returned, then an error has occurred, so we return "error"
    return "error"
}