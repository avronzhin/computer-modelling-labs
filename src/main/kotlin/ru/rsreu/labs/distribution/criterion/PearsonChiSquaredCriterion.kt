package ru.rsreu.labs.distribution.criterion

import ru.rsreu.labs.distribution.splitIntoSections
import kotlin.math.pow

fun getChiSquaredCriterionValue(
    values: List<Double>, k: Int, rangeStart: Double, rangeEnd: Double,
    f: (Int) -> Double
): Double {
    val sections = splitIntoSections(values, k, rangeStart, rangeEnd)
    val probabilities = MutableList(k) { index -> f(index) }
    var chiSquared = 0.0
    for (i in 0 until k) {
        val expected = values.size * probabilities[i]
        val actual = sections[i]
        chiSquared += (actual - expected).pow(2) / expected
    }
    return chiSquared
}