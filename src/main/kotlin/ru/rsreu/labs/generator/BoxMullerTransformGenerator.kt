package ru.rsreu.labs.generator

import kotlin.math.*

class BoxMullerTransformGenerator(
    private val generator: Generator, private val expectedValue: Double, private val dispersion: Double
) : Generator {

    private var cash: Double? = null

    override fun nextDouble(): Double {
        cash?.let {
            cash = null
            return it
        }

        val r1 = generator.nextDouble()
        val r2 = generator.nextDouble()
        val x = sqrt(-2 * ln(r1)) * cos(2 * PI * r2)
        val y = sqrt(-2 * ln(r1)) * sin(2 * PI * r2)
        cash = expectedValue + y * sqrt(dispersion)
        return expectedValue + x * sqrt(dispersion)
    }
}