package com.example.formfit.utils

import android.graphics.PointF
import kotlin.math.*

fun calculateAngle(a: PointF, b: PointF, c: PointF): Double {

    val angle = atan2(
        (c.y - b.y).toDouble(),
        (c.x - b.x).toDouble()
    ) - atan2(
        (a.y - b.y).toDouble(),
        (a.x - b.x).toDouble()
    )

    var degrees = Math.toDegrees(angle)

    if (degrees < 0) {
        degrees += 360
    }

    if (degrees > 180) {
        degrees = 360 - degrees
    }

    return degrees
}