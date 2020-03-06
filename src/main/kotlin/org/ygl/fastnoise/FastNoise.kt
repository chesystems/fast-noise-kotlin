package org.ygl.fastnoise



class FastNoise(
        var seed: Int = 1337
) {

    var frequency = 0.01f
    var interpolation = InterpolationType.QUINTIC
    var noiseType = NoiseType.SIMPLEX

    private var octaves = 3
    private val lacunarity = 2f
    private var gain = 0.5f
    private val fractalType = FractalType.FBM

    private var fractalBounding = 0f

    private val cellularDistanceFunction: CellularDistanceFunction = CellularDistanceFunction.EUCLIDEAN
    private val cellularReturnType = CellularReturnType.CELL_VALUE
    private val cellularNoiseLookup: FastNoise? = null

    private val gradientPerturbAmp = 1f / 0.45f

    init {
        calculateFractalBounding()
    }

    fun setFractalOctaves(octaves: Int) {
        this.octaves = octaves
        calculateFractalBounding()
    }

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
}