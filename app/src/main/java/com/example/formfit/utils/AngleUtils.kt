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

fun calculateZAngle(a: Point3F, b: Point3F, c: Point3F): Double {
    // Vector from b to a
    val ba = floatArrayOf(
        a.x - b.x,
        a.y - b.y,
        a.z - b.z
    )

    // Vector from b to c
    val bc = floatArrayOf(
        c.x - b.x,
        c.y - b.y,
        c.z - b.z
    )

    // Dot product
    val dot = ba[0]*bc[0] + ba[1]*bc[1] + ba[2]*bc[2]

    // Magnitudes
    val magES = sqrt((ba[0] * ba[0] + ba[1] * ba[1] + ba[2] * ba[2]).toDouble())
    val magEW = sqrt((bc[0]*bc[0] + bc[1]*bc[1] + bc[2]*bc[2]).toDouble())

    // Angle in radians → degrees
    val angleRad = acos(dot / (magES * magEW))
    return Math.toDegrees(angleRad)
}
