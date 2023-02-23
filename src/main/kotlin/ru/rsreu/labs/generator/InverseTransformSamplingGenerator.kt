package ru.rsreu.labs.generator

import kotlin.math.pow

class InverseTransformSamplingGenerator (
    private val generator: Generator
) : Generator{
    override fun nextDouble(): Double {
        val r = generator.nextDouble()
        return if(r < 0.5) f1(r) else f2(r)
    }

    private fun f1(r: Double) = r.pow(2)

    private fun f2(r: Double) = 4 * (r - 0.4375)
}