package ru.rsreu.labs.generator

import kotlin.math.sqrt

class CentralMarginalProfitGenerator(
    private val generator: Generator, private val expectedValue: Double, private val dispersion: Double
) : Generator {

    companion object {
        private const val N = 12
    }

    override fun nextDouble(): Double {
        val s = MutableList(N) { generator.nextDouble() }.sum()
        val x = (s - N.toDouble() / 2) / sqrt(N.toDouble() / 12)
        return expectedValue + x * sqrt(dispersion)
    }
}