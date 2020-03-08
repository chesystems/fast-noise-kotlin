package org.ygl.fastnoise



fun fastFloor(f: Float): Int {
    return if (f >= 0) f.toInt() else f.toInt() - 1
}

fun fastFloor(f: Double): Int {
    return if (f >= 0) f.toInt() else f.toInt() - 1
}

fun fastRound(f: Float): Int {
    return if (f >= 0) (f + 0.5f).toInt() else (f - 0.5f).toInt()
}

fun fastRound(d: Double): Int {
    return if (d >= 0) (d + 0.5).toInt() else (d - 0.5).toInt()
}