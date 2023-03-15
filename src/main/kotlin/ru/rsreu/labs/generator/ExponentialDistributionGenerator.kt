package ru.rsreu.labs.generator

import kotlin.math.ln

class ExponentialDistributionGenerator(
    private val uniformGenerator: UniformDistributionGenerator,
    private val lambda: Double
) : Generator {
    override fun nextDouble(): Double {
        val r = uniformGenerator.nextDouble()
        return - ln(r) / lambda
    }
}