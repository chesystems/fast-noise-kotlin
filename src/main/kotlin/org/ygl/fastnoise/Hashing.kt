package org.ygl.fastnoise


const val X_PRIME = 1619
const val Y_PRIME = 31337
const val Z_PRIME = 6971
const val W_PRIME = 1013

fun hash2D(seed: Int, x: Int, y: Int): Int {
    var hash = seed
    hash = hash xor X_PRIME * x
    hash = hash xor Y_PRIME * y
    hash *= hash * hash * 60493
    hash = hash shr 13 xor hash
    return hash
}

fun hash3D(seed: Int, x: Int, y: Int, z: Int): Int {
    var hash = seed
    hash = hash xor X_PRIME * x
    hash = hash xor Y_PRIME * y
    hash = hash xor Z_PRIME * z
    hash *= hash * hash * 60493
    hash = hash shr 13 xor hash
    return hash
}

fun hash4D(seed: Int, x: Int, y: Int, z: Int, w: Int): Int {
    var hash = seed
    hash = hash xor X_PRIME * x
    hash = hash xor Y_PRIME * y
    hash = hash xor Z_PRIME * z
    hash = hash xor W_PRIME * w
    hash *= hash * hash * 60493
    hash = hash shr 13 xor hash
    return hash
}

fun valCoord2D(seed: Int, x: Int, y: Int): Float {
    var n = seed
    n = n xor X_PRIME * x
    n = n xor Y_PRIME * y
    return n * n * n * 60493 / 2147483648.0.toFloat()
}

fun valCoord3D(seed: Int, x: Int, y: Int, z: Int): Float {
    var n = seed
    n = n xor X_PRIME * x
    n = n xor Y_PRIME * y
    n = n xor Z_PRIME * z
    return n * n * n * 60493 / 2147483648.0.toFloat()
}

fun valCoord4D(seed: Int, x: Int, y: Int, z: Int, w: Int): Float {
    var n = seed
    n = n xor X_PRIME * x
    n = n xor Y_PRIME * y
    n = n xor Z_PRIME * z
    n = n xor W_PRIME * w
    return n * n * n * 60493 / 2147483648.0.toFloat()
}

fun gradCoord2D(seed: Int, x: Int, y: Int, xd: Float, yd: Float): Float {
    var hash = seed
    hash = hash xor X_PRIME * x
    hash = hash xor Y_PRIME * y
    hash *= hash * hash * 60493
    hash = hash shr 13 xor hash
    val g: Float2 = gradient2D[hash and 7]
    return xd * g.x + yd * g.y
}

fun gradCoord3D(seed: Int, x: Int, y: Int, z: Int, xd: Float, yd: Float, zd: Float): Float {
    var hash = seed
    hash = hash xor X_PRIME * x
    hash = hash xor Y_PRIME * y
    hash = hash xor Z_PRIME * z
    hash *= hash * hash * 60493
    hash = hash shr 13 xor hash
    val g: Float3 = gradient3D[hash and 15]
    return xd * g.x + yd * g.y + zd * g.z
}

fun gradCoord3D(seed: Int, x: Int, y: Int, z: Int, xd: Double, yd: Double, zd: Double): Double {
    var hash = seed
    hash = hash xor X_PRIME * x
    hash = hash xor Y_PRIME * y
    hash = hash xor Z_PRIME * z
    hash *= hash * hash * 60493
    hash = hash shr 13 xor hash
    val g: Float3 = gradient3D[hash and 15]
    return xd * g.x + yd * g.y + zd * g.z
}

fun gradCoord4D(seed: Int, x: Int, y: Int, z: Int, w: Int, xd: Float, yd: Float, zd: Float, wd: Float): Float {
    var hash = seed
    hash = hash xor X_PRIME * x
    hash = hash xor Y_PRIME * y
    hash = hash xor Z_PRIME * z
    hash = hash xor W_PRIME * w
    hash *= hash * hash * 60493
    hash = hash shr 13 xor hash
    hash = hash and 31
    var a = yd
    var b = zd
    var c = wd // X,Y,Z
    when (hash shr 3) {
        1 -> {
            a = wd
            b = xd
            c = yd
        }
        2 -> {
            a = zd
            b = wd
            c = xd
        }
        3 -> {
            a = yd
            b = zd
            c = wd
        }
    }
    return (if (hash and 4 == 0) -a else a) + (if (hash and 2 == 0) -b else b) + if (hash and 1 == 0) -c else c
}

fun gradCoord4D(seed: Int, x: Int, y: Int, z: Int, w: Int, xd: Double, yd: Double, zd: Double, wd: Double): Double {
    var hash = seed
    hash = hash xor X_PRIME * x
    hash = hash xor Y_PRIME * y
    hash = hash xor Z_PRIME * z
    hash = hash xor W_PRIME * w
    hash *= hash * hash * 60493
    hash = hash shr 13 xor hash
    hash = hash and 31
    var a = yd
    var b = zd
    var c = wd // X,Y,Z
    when (hash shr 3) {
        1 -> {
            a = wd
            b = xd
            c = yd
        }
        2 -> {
            a = zd
            b = wd
            c = xd
        }
        3 -> {
            a = yd
            b = zd
            c = wd
        }
    }
    return (if (hash and 4 == 0) -a else a) + (if (hash and 2 == 0) -b else b) + if (hash and 1 == 0) -c else c
}