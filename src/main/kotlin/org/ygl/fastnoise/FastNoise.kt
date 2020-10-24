// FastNoise.java
//
// MIT License
//
// Copyright(c) 2017 Jordan Peck
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files(the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions :
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
// The developer's email is jorzixdan.me2@gzixmail.com (for great email, take
// off every 'zix'.)
//
package org.ygl.fastnoise

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class FastNoise(
        var seed: Int = 1337
) {

    var frequency = 0.01f
    var interpolationType = InterpolationType.QUINTIC
    var noiseType = NoiseType.SIMPLEX

    private var octaves = 3
    private var gain = 0.5f

    var lacunarity = 2.0f
    var fractalType = FractalType.FBM
    var fractalBounding = 0f

    var cellularDistanceFunction = CellularDistanceFunction.EUCLIDEAN
    var cellularReturnType = CellularReturnType.CELL_VALUE
    var cellularNoiseLookup: FastNoise? = null
    var gradientPerturbAmp = 1.0f / 0.45f

    init {
        calculateFractalBounding()
    }

    /**
     * Sets octave count for all fractal noise types
     * Default: 3
     */
    fun setFractalOctaves(octaves: Int) {
        this.octaves = octaves
        calculateFractalBounding()
    }

    /**
     * Sets octave gain for all fractal noise types
     * Default: 0.5
     */
    fun setFractalGain(gain: Float) {
        this.gain = gain
        calculateFractalBounding()
    }

    private fun calculateFractalBounding() {
        var amp = gain
        var ampFractal = 1f
        for (i in 1 until octaves) {
            ampFractal += amp
            amp *= gain
        }
        fractalBounding = 1 / ampFractal
    }

    fun getNoise3D(x1: Float, y1: Float, z1: Float): Float {
        val x = x1 * frequency
        val y = y1 * frequency
        val z = z1 * frequency

        return when (noiseType) {
            NoiseType.VALUE -> singleValue(seed, x, y, z)
            NoiseType.VALUE_FRACTAL -> when (fractalType) {
                FractalType.FBM -> singleValueFractalFBM(x, y, z)
                FractalType.BILLOW -> singleValueFractalBILLOW(x, y, z)
                FractalType.RIGID_MULTI -> singleValueFractalRigidMulti(x, y, z)
            }
            NoiseType.PERLIN -> singlePerlin(seed, x, y, z)
            NoiseType.PERLIN_FRACTAL -> when (fractalType) {
                FractalType.FBM -> singlePerlinFractalFBM(x, y, z)
                FractalType.BILLOW -> singlePerlinFractalBILLOW(x, y, z)
                FractalType.RIGID_MULTI -> singlePerlinFractalRigidMulti(x, y, z)
            }
            NoiseType.SIMPLEX -> singleSimplex(seed, x, y, z)
            NoiseType.SIMPLEX_FRACTAL -> when (fractalType) {
                FractalType.FBM -> singleSimplexFractalFBM(x, y, z)
                FractalType.BILLOW -> singleSimplexFractalBILLOW(x, y, z)
                FractalType.RIGID_MULTI -> singleSimplexFractalRigidMulti(x, y, z)
            }
            NoiseType.CELLULAR -> when (cellularReturnType) {
                CellularReturnType.CELL_VALUE,
                CellularReturnType.NOISE_LOOKUP,
                CellularReturnType.DISTANCE -> singleCellular(x, y, z)
                else -> singleCellular2Edge(x, y, z)
            }
            NoiseType.WHITE_NOISE -> getWhiteNoise(x, y, z)
            NoiseType.CUBIC -> singleCubic(seed, x, y, z)
            NoiseType.CUBIC_FRACTAL -> when (fractalType) {
                FractalType.FBM -> singleCubicFractalFBM(x, y, z)
                FractalType.BILLOW -> singleCubicFractalBillow(x, y, z)
                FractalType.RIGID_MULTI -> singleCubicFractalRigidMulti(x, y, z)
            }
        }
    }

    fun getNoise2D(x1: Float, y1: Float): Float {
        val x = x1 * frequency
        val y = y1 * frequency

        return when (noiseType) {
            NoiseType.VALUE -> singleValue(seed, x, y)
            NoiseType.VALUE_FRACTAL -> when (fractalType) {
                FractalType.FBM -> singleValueFractalFBM(x, y)
                FractalType.BILLOW -> singleValueFractalBILLOW(x, y)
                FractalType.RIGID_MULTI -> singleValueFractalRigidMulti(x, y)
            }
            NoiseType.PERLIN -> singlePerlin(seed, x, y)
            NoiseType.PERLIN_FRACTAL -> when (fractalType) {
                FractalType.FBM -> singlePerlinFractalFBM(x, y)
                FractalType.BILLOW -> singlePerlinFractalBILLOW(x, y)
                FractalType.RIGID_MULTI -> singlePerlinFractalRigidMulti(x, y)
            }
            NoiseType.SIMPLEX -> singleSimplex(seed, x, y)
            NoiseType.SIMPLEX_FRACTAL -> when (fractalType) {
                FractalType.FBM -> singleSimplexFractalFBM(x, y)
                FractalType.BILLOW -> singleSimplexFractalBILLOW(x, y)
                FractalType.RIGID_MULTI -> singleSimplexFractalRigidMulti(x, y)
            }
            NoiseType.CELLULAR -> when (cellularReturnType) {
                CellularReturnType.CELL_VALUE,
                CellularReturnType.NOISE_LOOKUP,
                CellularReturnType.DISTANCE -> singleCellular(x, y)
                else -> singleCellular2Edge(x, y)
            }
            NoiseType.WHITE_NOISE -> getWhiteNoise(x, y)
            NoiseType.CUBIC -> singleCubic(seed, x, y)
            NoiseType.CUBIC_FRACTAL -> when (fractalType) {
                FractalType.FBM -> singleCubicFractalFBM(x, y)
                FractalType.BILLOW -> singleCubicFractalBillow(x, y)
                FractalType.RIGID_MULTI -> singleCubicFractalRigidMulti(x, y)
            }
        }
    }

    // White Noise
    private fun floatCast2Int(f: Float): Int {
        val i = java.lang.Float.floatToRawIntBits(f)
        return i xor (i shr 16)
    }

    fun getWhiteNoise(x: Float, y: Float, z: Float, w: Float): Float {
        val xi = floatCast2Int(x)
        val yi = floatCast2Int(y)
        val zi = floatCast2Int(z)
        val wi = floatCast2Int(w)
        return valCoord4D(seed, xi, yi, zi, wi)
    }

    fun getWhiteNoise(x: Float, y: Float, z: Float): Float {
        val xi = floatCast2Int(x)
        val yi = floatCast2Int(y)
        val zi = floatCast2Int(z)
        return valCoord3D(seed, xi, yi, zi)
    }

    fun getWhiteNoise(x: Float, y: Float): Float {
        val xi = floatCast2Int(x)
        val yi = floatCast2Int(y)
        return valCoord2D(seed, xi, yi)
    }

    fun getWhiteNoiseInt(x: Int, y: Int, z: Int, w: Int): Float {
        return valCoord4D(seed, x, y, z, w)
    }

    fun getWhiteNoiseInt(x: Int, y: Int, z: Int): Float {
        return valCoord3D(seed, x, y, z)
    }

    fun GetWhiteNoiseInt(x: Int, y: Int): Float {
        return valCoord2D(seed, x, y)
    }

    // Value Noise
    fun getValueFractal(x1: Float, y1: Float, z1: Float): Float {
        val x = x1 * frequency
        val y = y1 * frequency
        val z = z1 * frequency

        return when (fractalType) {
            FractalType.FBM -> singleValueFractalFBM(x, y, z)
            FractalType.BILLOW -> singleValueFractalBILLOW(x, y, z)
            FractalType.RIGID_MULTI -> singleValueFractalRigidMulti(x, y, z)
        }
    }

    private fun singleValueFractalFBM(x: Float, y: Float, z: Float): Float {
        var x = x
        var y = y
        var z = z
        var seed = seed
        var sum = singleValue(seed, x, y, z)
        var amp = 1f
        for (i in 1 until octaves) {
            x *= lacunarity
            y *= lacunarity
            z *= lacunarity
            amp *= gain
            sum += singleValue(++seed, x, y, z) * amp
        }
        return sum * fractalBounding
    }

    private fun singleValueFractalBILLOW(x: Float, y: Float, z: Float): Float {
        var x = x
        var y = y
        var z = z
        var seed = seed
        var sum = abs(singleValue(seed, x, y, z)) * 2 - 1
        var amp = 1f
        for (i in 1 until octaves) {
            x *= lacunarity
            y *= lacunarity
            z *= lacunarity
            amp *= gain
            sum += (abs(singleValue(++seed, x, y, z)) * 2 - 1) * amp
        }
        return sum * fractalBounding
    }

    private fun singleValueFractalRigidMulti(x: Float, y: Float, z: Float): Float {
        var x = x
        var y = y
        var z = z
        var seed = seed
        var sum = 1 - abs(singleValue(seed, x, y, z))
        var amp = 1f
        for (i in 1 until octaves) {
            x *= lacunarity
            y *= lacunarity
            z *= lacunarity
            amp *= gain
            sum -= (1 - abs(singleValue(++seed, x, y, z))) * amp
        }
        return sum
    }

    fun getValue(x: Float, y: Float, z: Float): Float {
        return singleValue(seed, x * frequency, y * frequency, z * frequency)
    }

    private fun singleValue(seed: Int, x: Float, y: Float, z: Float): Float {
        val x0 = fastFloor(x)
        val y0 = fastFloor(y)
        val z0 = fastFloor(z)
        val x1 = x0 + 1
        val y1 = y0 + 1
        val z1 = z0 + 1
        val xs: Float
        val ys: Float
        val zs: Float

        when (interpolationType) {
            InterpolationType.LINEAR -> {
                xs = x - x0
                ys = y - y0
                zs = z - z0
            }
            InterpolationType.HERMITE -> {
                xs = interpolateHermite(x - x0)
                ys = interpolateHermite(y - y0)
                zs = interpolateHermite(z - z0)
            }
            InterpolationType.QUINTIC -> {
                xs = interpolateQuintic(x - x0)
                ys = interpolateQuintic(y - y0)
                zs = interpolateQuintic(z - z0)
            }
        }

        val xf00 = lerp(valCoord3D(seed, x0, y0, z0), valCoord3D(seed, x1, y0, z0), xs)
        val xf10 = lerp(valCoord3D(seed, x0, y1, z0), valCoord3D(seed, x1, y1, z0), xs)
        val xf01 = lerp(valCoord3D(seed, x0, y0, z1), valCoord3D(seed, x1, y0, z1), xs)
        val xf11 = lerp(valCoord3D(seed, x0, y1, z1), valCoord3D(seed, x1, y1, z1), xs)
        val yf0 = lerp(xf00, xf10, ys)
        val yf1 = lerp(xf01, xf11, ys)
        return lerp(yf0, yf1, zs)
    }

    fun getValueFractal(x1: Float, y1: Float): Float {
        val x = x1 * frequency
        val y = y1 * frequency

        return when (fractalType) {
            FractalType.FBM -> singleValueFractalFBM(x, y)
            FractalType.BILLOW -> singleValueFractalBILLOW(x, y)
            FractalType.RIGID_MULTI -> singleValueFractalRigidMulti(x, y)
        }
    }

    private fun singleValueFractalFBM(x: Float, y: Float): Float {
        var x = x
        var y = y
        var seed = seed
        var sum = singleValue(seed, x, y)
        var amp = 1f
        for (i in 1 until octaves) {
            x *= lacunarity
            y *= lacunarity
            amp *= gain
            sum += singleValue(++seed, x, y) * amp
        }
        return sum * fractalBounding
    }

    private fun singleValueFractalBILLOW(x1: Float, y1: Float): Float {
        var x = x1
        var y = y1
        var seed = seed
        var sum = abs(singleValue(seed, x, y)) * 2 - 1
        var amp = 1f
        for (i in 1 until octaves) {
            x *= lacunarity
            y *= lacunarity
            amp *= gain
            sum += (abs(singleValue(++seed, x, y)) * 2 - 1) * amp
        }
        return sum * fractalBounding
    }

    private fun singleValueFractalRigidMulti(x1: Float, y1: Float): Float {
        var x = x1
        var y = y1
        var seed = seed
        var sum = 1 - abs(singleValue(seed, x, y))
        var amp = 1f
        for (i in 1 until octaves) {
            x *= lacunarity
            y *= lacunarity
            amp *= gain
            sum -= (1 - abs(singleValue(++seed, x, y))) * amp
        }
        return sum
    }

    fun getValue(x: Float, y: Float): Float {
        return singleValue(seed, x * frequency, y * frequency)
    }

    private fun singleValue(seed: Int, x: Float, y: Float): Float {
        val x0 = fastFloor(x)
        val y0 = fastFloor(y)
        val x1 = x0 + 1
        val y1 = y0 + 1
        val xs: Float
        val ys: Float

        when (interpolationType) {
            InterpolationType.LINEAR -> {
                xs = x - x0
                ys = y - y0
            }
            InterpolationType.HERMITE -> {
                xs = interpolateHermite(x - x0)
                ys = interpolateHermite(y - y0)
            }
            InterpolationType.QUINTIC -> {
                xs = interpolateQuintic(x - x0)
                ys = interpolateQuintic(y - y0)
            }
        }
        val xf0 = lerp(valCoord2D(seed, x0, y0), valCoord2D(seed, x1, y0), xs)
        val xf1 = lerp(valCoord2D(seed, x0, y1), valCoord2D(seed, x1, y1), xs)
        return lerp(xf0, xf1, ys)
    }

    // Gradient Noise
    fun getPerlinFractal(x1: Float, y1: Float, z1: Float): Float {
        val x = x1 * frequency
        val y = y1 * frequency
        val z = z1 * frequency

        return when (fractalType) {
            FractalType.FBM -> singlePerlinFractalFBM(x, y, z)
            FractalType.BILLOW -> singlePerlinFractalBILLOW(x, y, z)
            FractalType.RIGID_MULTI -> singlePerlinFractalRigidMulti(x, y, z)
        }
    }

    private fun singlePerlinFractalFBM(x: Float, y: Float, z: Float): Float {
        var x = x
        var y = y
        var z = z
        var seed = seed
        var sum = singlePerlin(seed, x, y, z)
        var amp = 1f
        for (i in 1 until octaves) {
            x *= lacunarity
            y *= lacunarity
            z *= lacunarity
            amp *= gain
            sum += singlePerlin(++seed, x, y, z) * amp
        }
        return sum * fractalBounding
    }

    private fun singlePerlinFractalBILLOW(x: Float, y: Float, z: Float): Float {
        var x = x
        var y = y
        var z = z
        var seed = seed
        var sum = abs(singlePerlin(seed, x, y, z)) * 2 - 1
        var amp = 1f
        for (i in 1 until octaves) {
            x *= lacunarity
            y *= lacunarity
            z *= lacunarity
            amp *= gain
            sum += (abs(singlePerlin(++seed, x, y, z)) * 2 - 1) * amp
        }
        return sum * fractalBounding
    }

    private fun singlePerlinFractalRigidMulti(x: Float, y: Float, z: Float): Float {
        var x = x
        var y = y
        var z = z
        var seed = seed
        var sum = 1 - abs(singlePerlin(seed, x, y, z))
        var amp = 1f
        for (i in 1 until octaves) {
            x *= lacunarity
            y *= lacunarity
            z *= lacunarity
            amp *= gain
            sum -= (1 - abs(singlePerlin(++seed, x, y, z))) * amp
        }
        return sum
    }

    fun getPerlin(x: Float, y: Float, z: Float): Float {
        return singlePerlin(seed, x * frequency, y * frequency, z * frequency)
    }

    private fun singlePerlin(seed: Int, x: Float, y: Float, z: Float): Float {
        val x0 = fastFloor(x)
        val y0 = fastFloor(y)
        val z0 = fastFloor(z)
        val x1 = x0 + 1
        val y1 = y0 + 1
        val z1 = z0 + 1
        val xs: Float
        val ys: Float
        val zs: Float
        when (interpolationType) {
            InterpolationType.LINEAR -> {
                xs = x - x0
                ys = y - y0
                zs = z - z0
            }
            InterpolationType.HERMITE -> {
                xs = interpolateHermite(x - x0)
                ys = interpolateHermite(y - y0)
                zs = interpolateHermite(z - z0)
            }
            InterpolationType.QUINTIC -> {
                xs = interpolateQuintic(x - x0)
                ys = interpolateQuintic(y - y0)
                zs = interpolateQuintic(z - z0)
            }
        }
        val xd0 = x - x0
        val yd0 = y - y0
        val zd0 = z - z0
        val xd1 = xd0 - 1
        val yd1 = yd0 - 1
        val zd1 = zd0 - 1
        val xf00 = lerp(gradCoord3D(seed, x0, y0, z0, xd0, yd0, zd0), gradCoord3D(seed, x1, y0, z0, xd1, yd0, zd0), xs)
        val xf10 = lerp(gradCoord3D(seed, x0, y1, z0, xd0, yd1, zd0), gradCoord3D(seed, x1, y1, z0, xd1, yd1, zd0), xs)
        val xf01 = lerp(gradCoord3D(seed, x0, y0, z1, xd0, yd0, zd1), gradCoord3D(seed, x1, y0, z1, xd1, yd0, zd1), xs)
        val xf11 = lerp(gradCoord3D(seed, x0, y1, z1, xd0, yd1, zd1), gradCoord3D(seed, x1, y1, z1, xd1, yd1, zd1), xs)
        val yf0 = lerp(xf00, xf10, ys)
        val yf1 = lerp(xf01, xf11, ys)
        return lerp(yf0, yf1, zs)
    }

    fun getPerlinFractal(x1: Float, y1: Float): Float {
        val x = x1 * frequency
        val y = y1 * frequency

        return when (fractalType) {
            FractalType.FBM -> singlePerlinFractalFBM(x, y)
            FractalType.BILLOW -> singlePerlinFractalBILLOW(x, y)
            FractalType.RIGID_MULTI -> singlePerlinFractalRigidMulti(x, y)
        }
    }

    private fun singlePerlinFractalFBM(x: Float, y: Float): Float {
        var x = x
        var y = y
        var seed = seed
        var sum = singlePerlin(seed, x, y)
        var amp = 1f
        for (i in 1 until octaves) {
            x *= lacunarity
            y *= lacunarity
            amp *= gain
            sum += singlePerlin(++seed, x, y) * amp
        }
        return sum * fractalBounding
    }

    private fun singlePerlinFractalBILLOW(x: Float, y: Float): Float {
        var x = x
        var y = y
        var seed = seed
        var sum = abs(singlePerlin(seed, x, y)) * 2 - 1
        var amp = 1f
        for (i in 1 until octaves) {
            x *= lacunarity
            y *= lacunarity
            amp *= gain
            sum += (abs(singlePerlin(++seed, x, y)) * 2 - 1) * amp
        }
        return sum * fractalBounding
    }

    private fun singlePerlinFractalRigidMulti(x: Float, y: Float): Float {
        var x = x
        var y = y
        var seed = seed
        var sum = 1 - abs(singlePerlin(seed, x, y))
        var amp = 1f
        for (i in 1 until octaves) {
            x *= lacunarity
            y *= lacunarity
            amp *= gain
            sum -= (1 - abs(singlePerlin(++seed, x, y))) * amp
        }
        return sum
    }

    fun GetPerlin(x: Float, y: Float): Float {
        return singlePerlin(seed, x * frequency, y * frequency)
    }

    private fun singlePerlin(seed: Int, x: Float, y: Float): Float {
        val x0 = fastFloor(x)
        val y0 = fastFloor(y)
        val x1 = x0 + 1
        val y1 = y0 + 1
        val xs: Float
        val ys: Float
        when (interpolationType) {
            InterpolationType.LINEAR -> {
                xs = x - x0
                ys = y - y0
            }
            InterpolationType.HERMITE -> {
                xs = interpolateHermite(x - x0)
                ys = interpolateHermite(y - y0)
            }
            InterpolationType.QUINTIC -> {
                xs = interpolateQuintic(x - x0)
                ys = interpolateQuintic(y - y0)
            }
        }
        val xd0 = x - x0
        val yd0 = y - y0
        val xd1 = xd0 - 1
        val yd1 = yd0 - 1
        val xf0 = lerp(gradCoord2D(seed, x0, y0, xd0, yd0), gradCoord2D(seed, x1, y0, xd1, yd0), xs)
        val xf1 = lerp(gradCoord2D(seed, x0, y1, xd0, yd1), gradCoord2D(seed, x1, y1, xd1, yd1), xs)
        return lerp(xf0, xf1, ys)
    }

    // Simplex Noise
    fun getSimplexFractal(x1: Float, y1: Float, z1: Float): Float {
        val x = x1 * frequency
        val y = y1 * frequency
        val z = z1 * frequency
        return when (fractalType) {
            FractalType.FBM -> singleSimplexFractalFBM(x, y, z)
            FractalType.BILLOW -> singleSimplexFractalBILLOW(x, y, z)
            FractalType.RIGID_MULTI -> singleSimplexFractalRigidMulti(x, y, z)
        }
    }

    private fun singleSimplexFractalFBM(x: Float, y: Float, z: Float): Float {
        var x = x
        var y = y
        var z = z
        var seed = seed
        var sum = singleSimplex(seed, x, y, z)
        var amp = 1f
        for (i in 1 until octaves) {
            x *= lacunarity
            y *= lacunarity
            z *= lacunarity
            amp *= gain
            sum += singleSimplex(++seed, x, y, z) * amp
        }
        return sum * fractalBounding
    }

    private fun singleSimplexFractalBILLOW(x: Float, y: Float, z: Float): Float {
        var x = x
        var y = y
        var z = z
        var seed = seed
        var sum = abs(singleSimplex(seed, x, y, z)) * 2 - 1
        var amp = 1f
        for (i in 1 until octaves) {
            x *= lacunarity
            y *= lacunarity
            z *= lacunarity
            amp *= gain
            sum += (abs(singleSimplex(++seed, x, y, z)) * 2 - 1) * amp
        }
        return sum * fractalBounding
    }

    private fun singleSimplexFractalRigidMulti(x: Float, y: Float, z: Float): Float {
        var x = x
        var y = y
        var z = z
        var seed = seed
        var sum = 1 - abs(singleSimplex(seed, x, y, z))
        var amp = 1f
        for (i in 1 until octaves) {
            x *= lacunarity
            y *= lacunarity
            z *= lacunarity
            amp *= gain
            sum -= (1 - abs(singleSimplex(++seed, x, y, z))) * amp
        }
        return sum
    }

    fun getSimplex(x: Float, y: Float, z: Float): Float {
        return singleSimplex(seed, x * frequency, y * frequency, z * frequency)
    }

    fun getSimplex(x: Double, y: Double, z: Double): Double {
        return singleSimplex(seed, x * frequency, y * frequency, z * frequency)
    }

    private fun singleSimplex(seed: Int, x: Float, y: Float, z: Float): Float {
        var t = (x + y + z) * F3F
        val i = fastFloor(x + t)
        val j = fastFloor(y + t)
        val k = fastFloor(z + t)
        t = (i + j + k) * G3F
        val x0 = x - (i - t)
        val y0 = y - (j - t)
        val z0 = z - (k - t)
        val i1: Int
        val j1: Int
        val k1: Int
        val i2: Int
        val j2: Int
        val k2: Int
        if (x0 >= y0) {
            if (y0 >= z0) {
                i1 = 1
                j1 = 0
                k1 = 0
                i2 = 1
                j2 = 1
                k2 = 0
            } else if (x0 >= z0) {
                i1 = 1
                j1 = 0
                k1 = 0
                i2 = 1
                j2 = 0
                k2 = 1
            } else  // x0 < z0
            {
                i1 = 0
                j1 = 0
                k1 = 1
                i2 = 1
                j2 = 0
                k2 = 1
            }
        } else  // x0 < y0
        {
            if (y0 < z0) {
                i1 = 0
                j1 = 0
                k1 = 1
                i2 = 0
                j2 = 1
                k2 = 1
            } else if (x0 < z0) {
                i1 = 0
                j1 = 1
                k1 = 0
                i2 = 0
                j2 = 1
                k2 = 1
            } else  // x0 >= z0
            {
                i1 = 0
                j1 = 1
                k1 = 0
                i2 = 1
                j2 = 1
                k2 = 0
            }
        }
        val x1 = x0 - i1 + G3F
        val y1 = y0 - j1 + G3F
        val z1 = z0 - k1 + G3F
        val x2 = x0 - i2 + F3F
        val y2 = y0 - j2 + F3F
        val z2 = z0 - k2 + F3F
        val x3 = x0 + G33F
        val y3 = y0 + G33F
        val z3 = z0 + G33F
        val n0: Float
        val n1: Float
        val n2: Float
        val n3: Float
        t = 0.6f - x0 * x0 - y0 * y0 - z0 * z0
        if (t < 0) n0 = 0f else {
            t *= t
            n0 = t * t * gradCoord3D(seed, i, j, k, x0, y0, z0)
        }
        t = 0.6f - x1 * x1 - y1 * y1 - z1 * z1
        if (t < 0) n1 = 0f else {
            t *= t
            n1 = t * t * gradCoord3D(seed, i + i1, j + j1, k + k1, x1, y1, z1)
        }
        t = 0.6f - x2 * x2 - y2 * y2 - z2 * z2
        if (t < 0) n2 = 0f else {
            t *= t
            n2 = t * t * gradCoord3D(seed, i + i2, j + j2, k + k2, x2, y2, z2)
        }
        t = 0.6f - x3 * x3 - y3 * y3 - z3 * z3
        if (t < 0) n3 = 0f else {
            t *= t
            n3 = t * t * gradCoord3D(seed, i + 1, j + 1, k + 1, x3, y3, z3)
        }
        return 32 * (n0 + n1 + n2 + n3)
    }

    private fun singleSimplex(seed: Int, x: Double, y: Double, z: Double): Double {
        var t = (x + y + z) * F3D
        val i = fastFloor(x + t)
        val j = fastFloor(y + t)
        val k = fastFloor(z + t)
        t = (i + j + k) * G3D
        val x0 = x - (i - t)
        val y0 = y - (j - t)
        val z0 = z - (k - t)
        val i1: Int
        val j1: Int
        val k1: Int
        val i2: Int
        val j2: Int
        val k2: Int
        if (x0 >= y0) {
            if (y0 >= z0) {
                i1 = 1
                j1 = 0
                k1 = 0
                i2 = 1
                j2 = 1
                k2 = 0
            } else if (x0 >= z0) {
                i1 = 1
                j1 = 0
                k1 = 0
                i2 = 1
                j2 = 0
                k2 = 1
            } else {
                i1 = 0
                j1 = 0
                k1 = 1
                i2 = 1
                j2 = 0
                k2 = 1
            }
        } else {
            if (y0 < z0) {
                i1 = 0
                j1 = 0
                k1 = 1
                i2 = 0
                j2 = 1
                k2 = 1
            } else if (x0 < z0) {
                i1 = 0
                j1 = 1
                k1 = 0
                i2 = 0
                j2 = 1
                k2 = 1
            } else  // x0 >= z0
            {
                i1 = 0
                j1 = 1
                k1 = 0
                i2 = 1
                j2 = 1
                k2 = 0
            }
        }
        val x1 = x0 - i1 + G3D
        val y1 = y0 - j1 + G3D
        val z1 = z0 - k1 + G3D
        val x2 = x0 - i2 + F3D
        val y2 = y0 - j2 + F3D
        val z2 = z0 - k2 + F3D
        val x3 = x0 + G33D
        val y3 = y0 + G33D
        val z3 = z0 + G33D
        val n0: Double
        val n1: Double
        val n2: Double
        val n3: Double
        t = 0.6 - x0 * x0 - y0 * y0 - z0 * z0
        if (t < 0) n0 = 0.0 else {
            t *= t
            n0 = t * t * gradCoord3D(seed, i, j, k, x0, y0, z0)
        }
        t = 0.6 - x1 * x1 - y1 * y1 - z1 * z1
        if (t < 0) n1 = 0.0 else {
            t *= t
            n1 = t * t * gradCoord3D(seed, i + i1, j + j1, k + k1, x1, y1, z1)
        }
        t = 0.6 - x2 * x2 - y2 * y2 - z2 * z2
        if (t < 0) n2 = 0.0 else {
            t *= t
            n2 = t * t * gradCoord3D(seed, i + i2, j + j2, k + k2, x2, y2, z2)
        }
        t = 0.6 - x3 * x3 - y3 * y3 - z3 * z3
        if (t < 0) n3 = 0.0 else {
            t *= t
            n3 = t * t * gradCoord3D(seed, i + 1, j + 1, k + 1, x3, y3, z3)
        }
        return 32 * (n0 + n1 + n2 + n3)
    }

    fun getSimplexFractal(x1: Float, y1: Float): Float {
        val x = x1 * frequency
        val y = y1 * frequency
        return when (fractalType) {
            FractalType.FBM -> singleSimplexFractalFBM(x, y)
            FractalType.BILLOW -> singleSimplexFractalBILLOW(x, y)
            FractalType.RIGID_MULTI -> singleSimplexFractalRigidMulti(x, y)
        }
    }

    private fun singleSimplexFractalFBM(x: Float, y: Float): Float {
        var x = x
        var y = y
        var seed = seed
        var sum = singleSimplex(seed, x, y)
        var amp = 1f
        for (i in 1 until octaves) {
            x *= lacunarity
            y *= lacunarity
            amp *= gain
            sum += singleSimplex(++seed, x, y) * amp
        }
        return sum * fractalBounding
    }

    private fun singleSimplexFractalBILLOW(x: Float, y: Float): Float {
        var x = x
        var y = y
        var seed = seed
        var sum = abs(singleSimplex(seed, x, y)) * 2 - 1
        var amp = 1f
        for (i in 1 until octaves) {
            x *= lacunarity
            y *= lacunarity
            amp *= gain
            sum += (abs(singleSimplex(++seed, x, y)) * 2 - 1) * amp
        }
        return sum * fractalBounding
    }

    private fun singleSimplexFractalRigidMulti(x: Float, y: Float): Float {
        var x = x
        var y = y
        var seed = seed
        var sum = 1 - abs(singleSimplex(seed, x, y))
        var amp = 1f
        for (i in 1 until octaves) {
            x *= lacunarity
            y *= lacunarity
            amp *= gain
            sum -= (1 - abs(singleSimplex(++seed, x, y))) * amp
        }
        return sum
    }

    fun getSimplex(x: Float, y: Float): Float {
        return singleSimplex(seed, x * frequency, y * frequency)
    }

    fun getSimplex(x: Double, y: Double): Double {
        return singleSimplex(seed, x * frequency, y * frequency)
    }

    private fun singleSimplex(seed: Int, x: Float, y: Float): Float {
        var t = (x + y) * F2F
        val i = fastFloor(x + t)
        val j = fastFloor(y + t)
        t = (i + j) * G2F
        val X0 = i - t
        val Y0 = j - t
        val x0 = x - X0
        val y0 = y - Y0
        val i1: Int
        val j1: Int
        if (x0 > y0) {
            i1 = 1
            j1 = 0
        } else {
            i1 = 0
            j1 = 1
        }
        val x1 = x0 - i1 + G2F
        val y1 = y0 - j1 + G2F
        val x2 = x0 - 1 + F2F
        val y2 = y0 - 1 + F2F
        val n0: Float
        val n1: Float
        val n2: Float
        t = 0.5.toFloat() - x0 * x0 - y0 * y0
        if (t < 0) n0 = 0f else {
            t *= t
            n0 = t * t * gradCoord2D(seed, i, j, x0, y0)
        }
        t = 0.5.toFloat() - x1 * x1 - y1 * y1
        if (t < 0) n1 = 0f else {
            t *= t
            n1 = t * t * gradCoord2D(seed, i + i1, j + j1, x1, y1)
        }
        t = 0.5.toFloat() - x2 * x2 - y2 * y2
        if (t < 0) n2 = 0f else {
            t *= t
            n2 = t * t * gradCoord2D(seed, i + 1, j + 1, x2, y2)
        }
        return 50 * (n0 + n1 + n2)
    }

    private fun singleSimplex(seed: Int, x: Double, y: Double): Double {
        var t = (x + y) * F2D
        val i = fastFloor(x + t)
        val j = fastFloor(y + t)
        t = (i + j) * G2D
        val X0 = i - t
        val Y0 = j - t
        val x0 = x - X0
        val y0 = y - Y0
        val i1: Int
        val j1: Int
        if (x0 > y0) {
            i1 = 1
            j1 = 0
        } else {
            i1 = 0
            j1 = 1
        }
        val x1 = x0 - i1 + G2D
        val y1 = y0 - j1 + G2D
        val x2 = x0 - 1 + F2D
        val y2 = y0 - 1 + F2D
        val n0: Double
        val n1: Double
        val n2: Double
        t = 0.5 - x0 * x0 - y0 * y0
        if (t < 0) n0 = 0.0 else {
            t *= t
            n0 = t * t * gradCoord2D(seed, i, j, x0, y0)
        }
        t = 0.5 - x1 * x1 - y1 * y1
        if (t < 0) n1 = 0.0 else {
            t *= t
            n1 = t * t * gradCoord2D(seed, i + i1, j + j1, x1, y1)
        }
        t = 0.5 - x2 * x2 - y2 * y2
        if (t < 0) n2 = 0.0 else {
            t *= t
            n2 = t * t * gradCoord2D(seed, i + 1, j + 1, x2, y2)
        }
        return 50 * (n0 + n1 + n2)
    }

    fun getSimplex(x: Double, y: Double, z: Double, w: Double): Double {
        return singleSimplex(seed, x * frequency, y * frequency, z * frequency, w * frequency)
    }

    private fun singleSimplex(seed: Int, x: Double, y: Double, z: Double, w: Double): Double {
        val n0: Double
        val n1: Double
        val n2: Double
        val n3: Double
        val n4: Double
        var t = (x + y + z + w) * F4
        val i = fastFloor(x + t)
        val j = fastFloor(y + t)
        val k = fastFloor(z + t)
        val l = fastFloor(w + t)
        t = (i + j + k + l) * G4.toDouble()
        val X0 = i - t
        val Y0 = j - t
        val Z0 = k - t
        val W0 = l - t
        val x0 = x - X0
        val y0 = y - Y0
        val z0 = z - Z0
        val w0 = w - W0
        var c = if (x0 > y0) 32 else 0
        c += if (x0 > z0) 16 else 0
        c += if (y0 > z0) 8 else 0
        c += if (x0 > w0) 4 else 0
        c += if (y0 > w0) 2 else 0
        c += if (z0 > w0) 1 else 0
        c = c shl 2
        val i1 = if (SIMPLEX_4D[c] >= 3) 1 else 0
        val i2 = if (SIMPLEX_4D[c] >= 2) 1 else 0
        val i3 = if (SIMPLEX_4D[c++] >= 1) 1 else 0
        val j1 = if (SIMPLEX_4D[c] >= 3) 1 else 0
        val j2 = if (SIMPLEX_4D[c] >= 2) 1 else 0
        val j3 = if (SIMPLEX_4D[c++] >= 1) 1 else 0
        val k1 = if (SIMPLEX_4D[c] >= 3) 1 else 0
        val k2 = if (SIMPLEX_4D[c] >= 2) 1 else 0
        val k3 = if (SIMPLEX_4D[c++] >= 1) 1 else 0
        val l1 = if (SIMPLEX_4D[c] >= 3) 1 else 0
        val l2 = if (SIMPLEX_4D[c] >= 2) 1 else 0
        val l3 = if (SIMPLEX_4D[c] >= 1) 1 else 0
        val x1 = x0 - i1 + G4
        val y1 = y0 - j1 + G4
        val z1 = z0 - k1 + G4
        val w1 = w0 - l1 + G4
        val x2 = x0 - i2 + 2 * G4
        val y2 = y0 - j2 + 2 * G4
        val z2 = z0 - k2 + 2 * G4
        val w2 = w0 - l2 + 2 * G4
        val x3 = x0 - i3 + 3 * G4
        val y3 = y0 - j3 + 3 * G4
        val z3 = z0 - k3 + 3 * G4
        val w3 = w0 - l3 + 3 * G4
        val x4 = x0 - 1 + 4 * G4
        val y4 = y0 - 1 + 4 * G4
        val z4 = z0 - 1 + 4 * G4
        val w4 = w0 - 1 + 4 * G4
        t = 0.6.toFloat() - x0 * x0 - y0 * y0 - z0 * z0 - w0 * w0
        if (t < 0) n0 = 0.0 else {
            t *= t
            n0 = t * t * gradCoord4D(seed, i, j, k, l, x0, y0, z0, w0)
        }
        t = 0.6.toFloat() - x1 * x1 - y1 * y1 - z1 * z1 - w1 * w1
        if (t < 0) n1 = 0.0 else {
            t *= t
            n1 = t * t * gradCoord4D(seed, i + i1, j + j1, k + k1, l + l1, x1, y1, z1, w1)
        }
        t = 0.6.toFloat() - x2 * x2 - y2 * y2 - z2 * z2 - w2 * w2
        if (t < 0) n2 = 0.0 else {
            t *= t
            n2 = t * t * gradCoord4D(seed, i + i2, j + j2, k + k2, l + l2, x2, y2, z2, w2)
        }
        t = 0.6.toFloat() - x3 * x3 - y3 * y3 - z3 * z3 - w3 * w3
        if (t < 0) n3 = 0.0 else {
            t *= t
            n3 = t * t * gradCoord4D(seed, i + i3, j + j3, k + k3, l + l3, x3, y3, z3, w3)
        }
        t = 0.6.toFloat() - x4 * x4 - y4 * y4 - z4 * z4 - w4 * w4
        if (t < 0) n4 = 0.0 else {
            t *= t
            n4 = t * t * gradCoord4D(seed, i + 1, j + 1, k + 1, l + 1, x4, y4, z4, w4)
        }
        return 27 * (n0 + n1 + n2 + n3 + n4)
    }

    // Cubic Noise
    fun getCubicFractal(x1: Float, y1: Float, z1: Float): Float {
        val x = x1 * frequency
        val y = y1 * frequency
        val z = z1 * frequency
        return when (fractalType) {
            FractalType.FBM -> singleCubicFractalFBM(x, y, z)
            FractalType.BILLOW -> singleCubicFractalBillow(x, y, z)
            FractalType.RIGID_MULTI -> singleCubicFractalRigidMulti(x, y, z)
        }
    }

    private fun singleCubicFractalFBM(x: Float, y: Float, z: Float): Float {
        var x = x
        var y = y
        var z = z
        var seed = seed
        var sum = singleCubic(seed, x, y, z)
        var amp = 1f
        var i = 0
        while (++i < octaves) {
            x *= lacunarity
            y *= lacunarity
            z *= lacunarity
            amp *= gain
            sum += singleCubic(++seed, x, y, z) * amp
        }
        return sum * fractalBounding
    }

    private fun singleCubicFractalBillow(x: Float, y: Float, z: Float): Float {
        var x = x
        var y = y
        var z = z
        var seed = seed
        var sum = abs(singleCubic(seed, x, y, z)) * 2 - 1
        var amp = 1f
        var i = 0
        while (++i < octaves) {
            x *= lacunarity
            y *= lacunarity
            z *= lacunarity
            amp *= gain
            sum += (abs(singleCubic(++seed, x, y, z)) * 2 - 1) * amp
        }
        return sum * fractalBounding
    }

    private fun singleCubicFractalRigidMulti(x: Float, y: Float, z: Float): Float {
        var x = x
        var y = y
        var z = z
        var seed = seed
        var sum = 1 - abs(singleCubic(seed, x, y, z))
        var amp = 1f
        var i = 0
        while (++i < octaves) {
            x *= lacunarity
            y *= lacunarity
            z *= lacunarity
            amp *= gain
            sum -= (1 - abs(singleCubic(++seed, x, y, z))) * amp
        }
        return sum
    }

    fun getCubic(x: Float, y: Float, z: Float): Float {
        return singleCubic(seed, x * frequency, y * frequency, z * frequency)
    }

    private fun singleCubic(seed: Int, x: Float, y: Float, z: Float): Float {
        val x1 = fastFloor(x)
        val y1 = fastFloor(y)
        val z1 = fastFloor(z)
        val x0 = x1 - 1
        val y0 = y1 - 1
        val z0 = z1 - 1
        val x2 = x1 + 1
        val y2 = y1 + 1
        val z2 = z1 + 1
        val x3 = x1 + 2
        val y3 = y1 + 2
        val z3 = z1 + 2
        val xs = x - x1.toFloat()
        val ys = y - y1.toFloat()
        val zs = z - z1.toFloat()
        return cubicLerp(
                cubicLerp(
                        cubicLerp(valCoord3D(seed, x0, y0, z0), valCoord3D(seed, x1, y0, z0), valCoord3D(seed, x2, y0, z0), valCoord3D(seed, x3, y0, z0), xs),
                        cubicLerp(valCoord3D(seed, x0, y1, z0), valCoord3D(seed, x1, y1, z0), valCoord3D(seed, x2, y1, z0), valCoord3D(seed, x3, y1, z0), xs),
                        cubicLerp(valCoord3D(seed, x0, y2, z0), valCoord3D(seed, x1, y2, z0), valCoord3D(seed, x2, y2, z0), valCoord3D(seed, x3, y2, z0), xs),
                        cubicLerp(valCoord3D(seed, x0, y3, z0), valCoord3D(seed, x1, y3, z0), valCoord3D(seed, x2, y3, z0), valCoord3D(seed, x3, y3, z0), xs),
                        ys),
                cubicLerp(
                        cubicLerp(valCoord3D(seed, x0, y0, z1), valCoord3D(seed, x1, y0, z1), valCoord3D(seed, x2, y0, z1), valCoord3D(seed, x3, y0, z1), xs),
                        cubicLerp(valCoord3D(seed, x0, y1, z1), valCoord3D(seed, x1, y1, z1), valCoord3D(seed, x2, y1, z1), valCoord3D(seed, x3, y1, z1), xs),
                        cubicLerp(valCoord3D(seed, x0, y2, z1), valCoord3D(seed, x1, y2, z1), valCoord3D(seed, x2, y2, z1), valCoord3D(seed, x3, y2, z1), xs),
                        cubicLerp(valCoord3D(seed, x0, y3, z1), valCoord3D(seed, x1, y3, z1), valCoord3D(seed, x2, y3, z1), valCoord3D(seed, x3, y3, z1), xs),
                        ys),
                cubicLerp(
                        cubicLerp(valCoord3D(seed, x0, y0, z2), valCoord3D(seed, x1, y0, z2), valCoord3D(seed, x2, y0, z2), valCoord3D(seed, x3, y0, z2), xs),
                        cubicLerp(valCoord3D(seed, x0, y1, z2), valCoord3D(seed, x1, y1, z2), valCoord3D(seed, x2, y1, z2), valCoord3D(seed, x3, y1, z2), xs),
                        cubicLerp(valCoord3D(seed, x0, y2, z2), valCoord3D(seed, x1, y2, z2), valCoord3D(seed, x2, y2, z2), valCoord3D(seed, x3, y2, z2), xs),
                        cubicLerp(valCoord3D(seed, x0, y3, z2), valCoord3D(seed, x1, y3, z2), valCoord3D(seed, x2, y3, z2), valCoord3D(seed, x3, y3, z2), xs),
                        ys),
                cubicLerp(
                        cubicLerp(valCoord3D(seed, x0, y0, z3), valCoord3D(seed, x1, y0, z3), valCoord3D(seed, x2, y0, z3), valCoord3D(seed, x3, y0, z3), xs),
                        cubicLerp(valCoord3D(seed, x0, y1, z3), valCoord3D(seed, x1, y1, z3), valCoord3D(seed, x2, y1, z3), valCoord3D(seed, x3, y1, z3), xs),
                        cubicLerp(valCoord3D(seed, x0, y2, z3), valCoord3D(seed, x1, y2, z3), valCoord3D(seed, x2, y2, z3), valCoord3D(seed, x3, y2, z3), xs),
                        cubicLerp(valCoord3D(seed, x0, y3, z3), valCoord3D(seed, x1, y3, z3), valCoord3D(seed, x2, y3, z3), valCoord3D(seed, x3, y3, z3), xs),
                        ys),
                zs) * CUBIC_3D_BOUNDING
    }

    fun getCubicFractal(x1: Float, y1: Float): Float {
        val x = x1 * frequency
        val y = y1 * frequency
        return when (fractalType) {
            FractalType.FBM -> singleCubicFractalFBM(x, y)
            FractalType.BILLOW -> singleCubicFractalBillow(x, y)
            FractalType.RIGID_MULTI -> singleCubicFractalRigidMulti(x, y)
        }
    }

    private fun singleCubicFractalFBM(x: Float, y: Float): Float {
        var x = x
        var y = y
        var seed = seed
        var sum = singleCubic(seed, x, y)
        var amp = 1f
        var i = 0
        while (++i < octaves) {
            x *= lacunarity
            y *= lacunarity
            amp *= gain
            sum += singleCubic(++seed, x, y) * amp
        }
        return sum * fractalBounding
    }

    private fun singleCubicFractalBillow(x: Float, y: Float): Float {
        var x = x
        var y = y
        var seed = seed
        var sum = abs(singleCubic(seed, x, y)) * 2 - 1
        var amp = 1f
        var i = 0
        while (++i < octaves) {
            x *= lacunarity
            y *= lacunarity
            amp *= gain
            sum += (abs(singleCubic(++seed, x, y)) * 2 - 1) * amp
        }
        return sum * fractalBounding
    }

    private fun singleCubicFractalRigidMulti(x: Float, y: Float): Float {
        var x = x
        var y = y
        var seed = seed
        var sum = 1 - abs(singleCubic(seed, x, y))
        var amp = 1f
        var i = 0
        while (++i < octaves) {
            x *= lacunarity
            y *= lacunarity
            amp *= gain
            sum -= (1 - abs(singleCubic(++seed, x, y))) * amp
        }
        return sum
    }

    fun getCubic(x1: Float, y1: Float): Float {
        val x = x1 * frequency
        val y = y1 * frequency
        return singleCubic(0, x, y)
    }

    private fun singleCubic(seed: Int, x: Float, y: Float): Float {
        val x1 = fastFloor(x)
        val y1 = fastFloor(y)
        val x0 = x1 - 1
        val y0 = y1 - 1
        val x2 = x1 + 1
        val y2 = y1 + 1
        val x3 = x1 + 2
        val y3 = y1 + 2
        val xs = x - x1.toFloat()
        val ys = y - y1.toFloat()

        return cubicLerp(
                cubicLerp(valCoord2D(seed, x0, y0), valCoord2D(seed, x1, y0), valCoord2D(seed, x2, y0), valCoord2D(seed, x3, y0), xs),
                cubicLerp(valCoord2D(seed, x0, y1), valCoord2D(seed, x1, y1), valCoord2D(seed, x2, y1), valCoord2D(seed, x3, y1), xs),
                cubicLerp(valCoord2D(seed, x0, y2), valCoord2D(seed, x1, y2), valCoord2D(seed, x2, y2), valCoord2D(seed, x3, y2), xs),
                cubicLerp(valCoord2D(seed, x0, y3), valCoord2D(seed, x1, y3), valCoord2D(seed, x2, y3), valCoord2D(seed, x3, y3), xs),
                ys) * CUBIC_2D_BOUNDING
    }

    // Cellular Noise
    fun getCellular(x1: Float, y1: Float, z1: Float): Float {
        val x = x1 * frequency
        val y = y1 * frequency
        val z = z1 * frequency

        return when (cellularReturnType) {
            CellularReturnType.CELL_VALUE,
            CellularReturnType.NOISE_LOOKUP,
            CellularReturnType.DISTANCE -> singleCellular(x, y, z)
            else -> singleCellular2Edge(x, y, z)
        }
    }

    private fun singleCellular(x: Float, y: Float, z: Float): Float {
        val xr = fastRound(x)
        val yr = fastRound(y)
        val zr = fastRound(z)
        var distance = 999999f
        var xc = 0
        var yc = 0
        var zc = 0
        when (cellularDistanceFunction) {
            CellularDistanceFunction.EUCLIDEAN -> {
                var xi = xr - 1
                while (xi <= xr + 1) {
                    var yi = yr - 1
                    while (yi <= yr + 1) {
                        var zi = zr - 1
                        while (zi <= zr + 1) {
                            val vec = cell3D[hash3D(seed, xi, yi, zi) and 255]
                            val vecX = xi - x + vec.x
                            val vecY = yi - y + vec.y
                            val vecZ = zi - z + vec.z
                            val newDistance = vecX * vecX + vecY * vecY + vecZ * vecZ
                            if (newDistance < distance) {
                                distance = newDistance
                                xc = xi
                                yc = yi
                                zc = zi
                            }
                            zi++
                        }
                        yi++
                    }
                    xi++
                }
            }
            CellularDistanceFunction.MANHATTAN -> {
                var xi = xr - 1
                while (xi <= xr + 1) {
                    var yi = yr - 1
                    while (yi <= yr + 1) {
                        var zi = zr - 1
                        while (zi <= zr + 1) {
                            val vec = cell3D[hash3D(seed, xi, yi, zi) and 255]
                            val vecX = xi - x + vec.x
                            val vecY = yi - y + vec.y
                            val vecZ = zi - z + vec.z
                            val newDistance = abs(vecX) + abs(vecY) + abs(vecZ)
                            if (newDistance < distance) {
                                distance = newDistance
                                xc = xi
                                yc = yi
                                zc = zi
                            }
                            zi++
                        }
                        yi++
                    }
                    xi++
                }
            }
            CellularDistanceFunction.NATURAL -> {
                var xi = xr - 1
                while (xi <= xr + 1) {
                    var yi = yr - 1
                    while (yi <= yr + 1) {
                        var zi = zr - 1
                        while (zi <= zr + 1) {
                            val vec = cell3D[hash3D(seed, xi, yi, zi) and 255]
                            val vecX = xi - x + vec.x
                            val vecY = yi - y + vec.y
                            val vecZ = zi - z + vec.z
                            val newDistance = abs(vecX) + abs(vecY) + abs(vecZ) + (vecX * vecX + vecY * vecY + vecZ * vecZ)
                            if (newDistance < distance) {
                                distance = newDistance
                                xc = xi
                                yc = yi
                                zc = zi
                            }
                            zi++
                        }
                        yi++
                    }
                    xi++
                }
            }
        }
        return when (cellularReturnType) {
            CellularReturnType.CELL_VALUE -> valCoord3D(0, xc, yc, zc)
            CellularReturnType.NOISE_LOOKUP -> {
                val vec = cell3D[hash3D(seed, xc, yc, zc) and 255]
                cellularNoiseLookup!!.getNoise3D(xc + vec.x, yc + vec.y, zc + vec.z)
            }
            CellularReturnType.DISTANCE -> distance - 1
            else -> 0f
        }
    }

    private fun singleCellular2Edge(x: Float, y: Float, z: Float): Float {
        val xr = fastRound(x)
        val yr = fastRound(y)
        val zr = fastRound(z)
        var distance = 999999f
        var distance2 = 999999f
        when (cellularDistanceFunction) {
            CellularDistanceFunction.EUCLIDEAN -> {
                var xi = xr - 1
                while (xi <= xr + 1) {
                    var yi = yr - 1
                    while (yi <= yr + 1) {
                        var zi = zr - 1
                        while (zi <= zr + 1) {
                            val vec = cell3D[hash3D(seed, xi, yi, zi) and 255]
                            val vecX = xi - x + vec.x
                            val vecY = yi - y + vec.y
                            val vecZ = zi - z + vec.z
                            val newDistance = vecX * vecX + vecY * vecY + vecZ * vecZ
                            distance2 = max(min(distance2, newDistance), distance)
                            distance = min(distance, newDistance)
                            zi++
                        }
                        yi++
                    }
                    xi++
                }
            }
            CellularDistanceFunction.MANHATTAN -> {
                var xi = xr - 1
                while (xi <= xr + 1) {
                    var yi = yr - 1
                    while (yi <= yr + 1) {
                        var zi = zr - 1
                        while (zi <= zr + 1) {
                            val vec = cell3D[hash3D(seed, xi, yi, zi) and 255]
                            val vecX = xi - x + vec.x
                            val vecY = yi - y + vec.y
                            val vecZ = zi - z + vec.z
                            val newDistance = abs(vecX) + abs(vecY) + abs(vecZ)
                            distance2 = max(min(distance2, newDistance), distance)
                            distance = min(distance, newDistance)
                            zi++
                        }
                        yi++
                    }
                    xi++
                }
            }
            CellularDistanceFunction.NATURAL -> {
                var xi = xr - 1
                while (xi <= xr + 1) {
                    var yi = yr - 1
                    while (yi <= yr + 1) {
                        var zi = zr - 1
                        while (zi <= zr + 1) {
                            val vec = cell3D[hash3D(seed, xi, yi, zi) and 255]
                            val vecX = xi - x + vec.x
                            val vecY = yi - y + vec.y
                            val vecZ = zi - z + vec.z
                            val newDistance = abs(vecX) + abs(vecY) + abs(vecZ) + (vecX * vecX + vecY * vecY + vecZ * vecZ)
                            distance2 = max(min(distance2, newDistance), distance)
                            distance = min(distance, newDistance)
                            zi++
                        }
                        yi++
                    }
                    xi++
                }
            }
        }
        return when (cellularReturnType) {
            CellularReturnType.DISTANCE2 -> distance2 - 1
            CellularReturnType.DISTANCE2_ADD -> distance2 + distance - 1
            CellularReturnType.DISTANCE2_SUB -> distance2 - distance - 1
            CellularReturnType.DISTANCE2_MUL -> distance2 * distance - 1
            CellularReturnType.DISTANCE2_DIV -> distance / distance2 - 1
            else -> 0f
        }
    }

    fun getCellular(x1: Float, y1: Float): Float {
        val x = x1 * frequency
        val y = y1 * frequency
        return when (cellularReturnType) {
            CellularReturnType.CELL_VALUE,
            CellularReturnType.NOISE_LOOKUP,
            CellularReturnType.DISTANCE -> singleCellular(x, y)
            else -> singleCellular2Edge(x, y)
        }
    }

    private fun singleCellular(x: Float, y: Float): Float {
        val xr = fastRound(x)
        val yr = fastRound(y)
        var distance = 999999f
        var xc = 0
        var yc = 0
        when (cellularDistanceFunction) {
            CellularDistanceFunction.EUCLIDEAN -> {
                var xi = xr - 1
                while (xi <= xr + 1) {
                    var yi = yr - 1
                    while (yi <= yr + 1) {
                        val vec = cell2D[hash2D(seed, xi, yi) and 255]
                        val vecX = xi - x + vec.x
                        val vecY = yi - y + vec.y
                        val newDistance = vecX * vecX + vecY * vecY
                        if (newDistance < distance) {
                            distance = newDistance
                            xc = xi
                            yc = yi
                        }
                        yi++
                    }
                    xi++
                }
            }
            CellularDistanceFunction.MANHATTAN -> {
                var xi = xr - 1
                while (xi <= xr + 1) {
                    var yi = yr - 1
                    while (yi <= yr + 1) {
                        val vec = cell2D[hash2D(seed, xi, yi) and 255]
                        val vecX = xi - x + vec.x
                        val vecY = yi - y + vec.y
                        val newDistance = abs(vecX) + abs(vecY)
                        if (newDistance < distance) {
                            distance = newDistance
                            xc = xi
                            yc = yi
                        }
                        yi++
                    }
                    xi++
                }
            }
            CellularDistanceFunction.NATURAL -> {
                var xi = xr - 1
                while (xi <= xr + 1) {
                    var yi = yr - 1
                    while (yi <= yr + 1) {
                        val vec = cell2D[hash2D(seed, xi, yi) and 255]
                        val vecX = xi - x + vec.x
                        val vecY = yi - y + vec.y
                        val newDistance = abs(vecX) + abs(vecY) + (vecX * vecX + vecY * vecY)
                        if (newDistance < distance) {
                            distance = newDistance
                            xc = xi
                            yc = yi
                        }
                        yi++
                    }
                    xi++
                }
            }
        }
        return when (cellularReturnType) {
            CellularReturnType.CELL_VALUE -> valCoord2D(0, xc, yc)
            CellularReturnType.NOISE_LOOKUP -> {
                val vec = cell2D[hash2D(seed, xc, yc) and 255]
                cellularNoiseLookup!!.getNoise2D(xc + vec.x, yc + vec.y)
            }
            CellularReturnType.DISTANCE -> distance - 1
            else -> 0f
        }
    }

    private fun singleCellular2Edge(x: Float, y: Float): Float {
        val xr = fastRound(x)
        val yr = fastRound(y)
        var distance = 999999f
        var distance2 = 999999f
        when (cellularDistanceFunction) {
            CellularDistanceFunction.EUCLIDEAN -> {
                var xi = xr - 1
                while (xi <= xr + 1) {
                    var yi = yr - 1
                    while (yi <= yr + 1) {
                        val vec = cell2D[hash2D(seed, xi, yi) and 255]
                        val vecX = xi - x + vec.x
                        val vecY = yi - y + vec.y
                        val newDistance = vecX * vecX + vecY * vecY
                        distance2 = max(min(distance2, newDistance), distance)
                        distance = min(distance, newDistance)
                        yi++
                    }
                    xi++
                }
            }
            CellularDistanceFunction.MANHATTAN -> {
                var xi = xr - 1
                while (xi <= xr + 1) {
                    var yi = yr - 1
                    while (yi <= yr + 1) {
                        val vec = cell2D[hash2D(seed, xi, yi) and 255]
                        val vecX = xi - x + vec.x
                        val vecY = yi - y + vec.y
                        val newDistance = abs(vecX) + abs(vecY)
                        distance2 = max(min(distance2, newDistance), distance)
                        distance = min(distance, newDistance)
                        yi++
                    }
                    xi++
                }
            }
            CellularDistanceFunction.NATURAL -> {
                var xi = xr - 1
                while (xi <= xr + 1) {
                    var yi = yr - 1
                    while (yi <= yr + 1) {
                        val vec = cell2D[hash2D(seed, xi, yi) and 255]
                        val vecX = xi - x + vec.x
                        val vecY = yi - y + vec.y
                        val newDistance = abs(vecX) + abs(vecY) + (vecX * vecX + vecY * vecY)
                        distance2 = max(min(distance2, newDistance), distance)
                        distance = min(distance, newDistance)
                        yi++
                    }
                    xi++
                }
            }
        }
        return when (cellularReturnType) {
            CellularReturnType.DISTANCE2 -> distance2 - 1
            CellularReturnType.DISTANCE2_ADD -> distance2 + distance - 1
            CellularReturnType.DISTANCE2_SUB -> distance2 - distance - 1
            CellularReturnType.DISTANCE2_MUL -> distance2 * distance - 1
            CellularReturnType.DISTANCE2_DIV -> distance / distance2 - 1
            else -> 0f
        }
    }

    fun gradientPerturb(v3: Vec3f) {
        singleGradientPerturb(seed, gradientPerturbAmp, frequency, v3)
    }

    fun gradientPerturbFractal(v3: Vec3f) {
        var seed = seed
        var amp = gradientPerturbAmp * fractalBounding
        var freq = frequency
        singleGradientPerturb(seed, amp, frequency, v3)
        for (i in 1 until octaves) {
            freq *= lacunarity
            amp *= gain
            singleGradientPerturb(++seed, amp, freq, v3)
        }
    }

    private fun singleGradientPerturb(seed: Int, perturbAmp: Float, frequency: Float, v3: Vec3f) {
        val xf: Float = v3.x * frequency
        val yf: Float = v3.y * frequency
        val zf: Float = v3.z * frequency
        val x0 = fastFloor(xf)
        val y0 = fastFloor(yf)
        val z0 = fastFloor(zf)
        val x1 = x0 + 1
        val y1 = y0 + 1
        val z1 = z0 + 1
        val xs: Float
        val ys: Float
        val zs: Float
        when (interpolationType) {
            InterpolationType.LINEAR -> {
                xs = xf - x0
                ys = yf - y0
                zs = zf - z0
            }
            InterpolationType.HERMITE -> {
                xs = interpolateHermite(xf - x0)
                ys = interpolateHermite(yf - y0)
                zs = interpolateHermite(zf - z0)
            }
            InterpolationType.QUINTIC -> {
                xs = interpolateQuintic(xf - x0)
                ys = interpolateQuintic(yf - y0)
                zs = interpolateQuintic(zf - z0)
            }
        }
        var vec0 = cell3D[hash3D(seed, x0, y0, z0) and 255]
        var vec1 = cell3D[hash3D(seed, x1, y0, z0) and 255]
        var lx0x = lerp(vec0.x, vec1.x, xs)
        var ly0x = lerp(vec0.y, vec1.y, xs)
        var lz0x = lerp(vec0.z, vec1.z, xs)
        vec0 = cell3D[hash3D(seed, x0, y1, z0) and 255]
        vec1 = cell3D[hash3D(seed, x1, y1, z0) and 255]
        var lx1x = lerp(vec0.x, vec1.x, xs)
        var ly1x = lerp(vec0.y, vec1.y, xs)
        var lz1x = lerp(vec0.z, vec1.z, xs)
        val lx0y = lerp(lx0x, lx1x, ys)
        val ly0y = lerp(ly0x, ly1x, ys)
        val lz0y = lerp(lz0x, lz1x, ys)
        vec0 = cell3D[hash3D(seed, x0, y0, z1) and 255]
        vec1 = cell3D[hash3D(seed, x1, y0, z1) and 255]
        lx0x = lerp(vec0.x, vec1.x, xs)
        ly0x = lerp(vec0.y, vec1.y, xs)
        lz0x = lerp(vec0.z, vec1.z, xs)
        vec0 = cell3D[hash3D(seed, x0, y1, z1) and 255]
        vec1 = cell3D[hash3D(seed, x1, y1, z1) and 255]
        lx1x = lerp(vec0.x, vec1.x, xs)
        ly1x = lerp(vec0.y, vec1.y, xs)
        lz1x = lerp(vec0.z, vec1.z, xs)
        v3.x += lerp(lx0y, lerp(lx0x, lx1x, ys), zs) * perturbAmp
        v3.y += lerp(ly0y, lerp(ly0x, ly1x, ys), zs) * perturbAmp
        v3.z += lerp(lz0y, lerp(lz0x, lz1x, ys), zs) * perturbAmp
    }

    fun gradientPerturb(v2: Vec2f) {
        singleGradientPerturb(seed, gradientPerturbAmp, frequency, v2)
    }

    fun gradientPerturbFractal(v2: Vec2f) {
        var seed = seed
        var amp = gradientPerturbAmp * fractalBounding
        var freq = frequency
        singleGradientPerturb(seed, amp, frequency, v2)
        for (i in 1 until octaves) {
            freq *= lacunarity
            amp *= gain
            singleGradientPerturb(++seed, amp, freq, v2)
        }
    }

    private fun singleGradientPerturb(seed: Int, perturbAmp: Float, frequency: Float, v2: Vec2f) {
        val xf = v2.x * frequency
        val yf = v2.y * frequency
        val x0 = fastFloor(xf)
        val y0 = fastFloor(yf)
        val x1 = x0 + 1
        val y1 = y0 + 1
        val xs: Float
        val ys: Float
        when (interpolationType) {
            InterpolationType.LINEAR -> {
                xs = xf - x0
                ys = yf - y0
            }
            InterpolationType.HERMITE -> {
                xs = interpolateHermite(xf - x0)
                ys = interpolateHermite(yf - y0)
            }
            InterpolationType.QUINTIC -> {
                xs = interpolateQuintic(xf - x0)
                ys = interpolateQuintic(yf - y0)
            }
        }
        var vec0 = cell2D[hash2D(seed, x0, y0) and 255]
        var vec1 = cell2D[hash2D(seed, x1, y0) and 255]
        val lx0x = lerp(vec0.x, vec1.x, xs)
        val ly0x = lerp(vec0.y, vec1.y, xs)
        vec0 = cell2D[hash2D(seed, x0, y1) and 255]
        vec1 = cell2D[hash2D(seed, x1, y1) and 255]
        val lx1x = lerp(vec0.x, vec1.x, xs)
        val ly1x = lerp(vec0.y, vec1.y, xs)
        v2.x += lerp(lx0x, lx1x, ys) * perturbAmp
        v2.y += lerp(ly0x, ly1x, ys) * perturbAmp
    }

    companion object {
        // Returns a 0 float/double
        fun GetDecimalType() = 0f

        private const val F3D = 1.0 / 3.0
        private const val F3F = F3D.toFloat()
        private const val G3D = 1.0 / 6.0
        private const val G3F = G3D.toFloat()
        private const val G33D = G3D * 3 - 1
        private const val G33F = G3F * 3 - 1
        private const val F2D = 1.0 / 2.0
        private const val F2F = F2D.toFloat()
        private const val G2D = 1.0 / 4.0
        private const val G2F = G2D.toFloat()
        private val SIMPLEX_4D = byteArrayOf(
                0, 1, 2, 3, 0, 1, 3, 2, 0, 0, 0, 0, 0, 2, 3, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 0,
                0, 2, 1, 3, 0, 0, 0, 0, 0, 3, 1, 2, 0, 3, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 3, 2, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                1, 2, 0, 3, 0, 0, 0, 0, 1, 3, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 3, 0, 1, 2, 3, 1, 0,
                1, 0, 2, 3, 1, 0, 3, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 3, 1, 0, 0, 0, 0, 2, 1, 3, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                2, 0, 1, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 1, 2, 3, 0, 2, 1, 0, 0, 0, 0, 3, 1, 2, 0,
                2, 1, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 1, 0, 2, 0, 0, 0, 0, 3, 2, 0, 1, 3, 2, 1, 0
        )
        private const val F4 = ((2.23606797 - 1.0) / 4.0).toFloat()
        private const val G4 = ((5.0 - 2.23606797) / 20.0).toFloat()
        private const val CUBIC_3D_BOUNDING = 1 / (1.5f * 1.5f * 1.5f)
        private const val CUBIC_2D_BOUNDING = 1 / (1.5f * 1.5f)
    }

}
