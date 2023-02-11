package ru.rsreu.labs.distribution

import kotlin.math.pow

fun List<Double>.getProbabilityDistributionEstimations(): DistributionParametersEstimations {
    val expectedValue = this.getMoment(1)
    val secondMoment = this.getMoment(2)
    val dispersion = secondMoment - expectedValue.pow(2)
    val thirdMoment = this.getMoment(3)
    return DistributionParametersEstimations(
        expectedValue,
        dispersion,
        secondMoment,
        thirdMoment
    )
}

private fun List<Double>.getMoment(number: Int) = this.sumOf { it.pow(number) } / this.size

data class DistributionParametersEstimations(
    val expectedValue: Double,
    val dispersion: Double,
    val secondMoment: Double,
    val thirdMoment: Double
)