package org.ygl.fastnoise


fun lerp(a: Float, b: Float, t: Float): Float {
    return a + t * (b - a)
}

fun lerp(a: Double, b: Double, t: Double): Double {
    return a + t * (b - a)
}

fun interpolateHermite(t: Float): Float {
    return t * t * (3 - 2 * t)
}

fun interpolateHermite(t: Double): Double {
    return t * t * (3 - 2 * t)
}

fun interpolateQuintic(t: Float): Float {
    return t * t * t * (t * (t * 6 - 15) + 10)
}

fun interpolateQuintic(t: Double): Double {
    return t * t * t * (t * (t * 6 - 15) + 10)
}

fun cubicLerp(a: Float, b: Float, c: Float, d: Float, t: Float): Float {
    val p = d - c - (a - b)
    val tt = t * t
    return t * tt * p + tt * (a - b - p) + t * (c - a) + b
}

fun cubicLerp(a: Double, b: Double, c: Double, d: Double, t: Double): Double {
    val p = d - c - (a - b)
    val tt = t * t
    return t * tt * p + tt * (a - b - p) + t * (c - a) + b
}