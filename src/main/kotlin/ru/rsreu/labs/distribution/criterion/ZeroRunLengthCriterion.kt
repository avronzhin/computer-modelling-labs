package ru.rsreu.labs.distribution.criterion

import kotlin.math.pow
import kotlin.math.sqrt

fun getZeroRunLengthCriterionValue(values: List<Double>, p: Double, tB: Double): RunCriterionInfo {
    val y = values.map { if (it < p) 0 else 1 }
    val zeroSeriesCount = getZeroSeriesCount(y)
    val zeroCount = y.filter { it == 0 }.size

    val expectedValue = (1 - p) / p + 1
    val dispersion = (1 - p) / p.pow(2)
    val avgZeroValue = zeroCount.toDouble() / zeroSeriesCount

    val vH = expectedValue - tB * sqrt(dispersion / zeroSeriesCount)
    val vB = expectedValue + tB * sqrt(dispersion / zeroSeriesCount)
    return RunCriterionInfo(avgZeroValue, vH, vB)
}

data class RunCriterionInfo(
    val value: Double, val vH: Double, val vB: Double
)

private fun getZeroSeriesCount(y: List<Int>): Int {
    var k0 = 0
    for (i in 1 until y.size) {
        if ((y[i - 1] == 0) && (y[i] == 1)) k0++
    }
    if (y.last() == 0) k0++
    return k0
}