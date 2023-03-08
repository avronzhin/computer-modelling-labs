package ru.rsreu.labs

import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sqrt

fun getConfidenceInterval(values: List<Double>, t: Double): ConfidenceInterval {
    val expectedValue = getExpectedValue(values)
    val dispersion = getDispersion(values, expectedValue)
    val standardDeviation = sqrt(dispersion)
    val accuracy = t * standardDeviation / sqrt(PI)
    return ConfidenceInterval(expectedValue - accuracy, expectedValue + accuracy)
}

fun getExpectedValue(values: List<Double>): Double {
    return values.average()
}

fun getDispersion(values: List<Double>, expectedValue: Double = getExpectedValue(values)) : Double {
    return values.map { (it - expectedValue).pow(2) }.average()
}

data class ConfidenceInterval(
    val intervalStart: Double, val intervalFinish: Double
) {
    override fun toString(): String {
        return "[$intervalStart, $intervalFinish]"
    }
}