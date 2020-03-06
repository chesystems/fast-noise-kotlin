package org.ygl.fastnoise


private val gradient3D: Array<Float3> = arrayOf(
        Float3(1f, 1f, 1f), Float3(-1f, 1f, 1f), Float3(1f, -1f, 1f), Float3(-1f, -1f, 1f),
        Float3(1f, 1f, 1f), Float3(-1f, 1f, 1f), Float3(1f, 1f, -1f), Float3(-1f, 1f, -1f),
        Float3(1f, 1f, 1f), Float3(1f, -1f, 1f), Float3(1f, 1f, -1f), Float3(1f, -1f, -1f),
        Float3(1f, 1f, 1f), Float3(1f, -1f, 1f), Float3(-1f, 1f, 1f), Float3(1f, -1f, -1f)
)