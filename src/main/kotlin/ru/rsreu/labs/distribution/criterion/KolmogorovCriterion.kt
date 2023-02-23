package ru.rsreu.labs.distribution.criterion

import kotlin.math.absoluteValue
import kotlin.math.sqrt

fun getKolmogorovCriterionValue(values: List<Double>): Double {
    var maxD = Double.MIN_VALUE
    val sortedValues = values.sorted()

    val n = values.size
    val theoreticalFunction = { value: Double ->
        if (value < 0) 0.0
        else if (value < 1) value
        else 1.0
    }

    for (i in values.indices) {
        val theoreticalFunctionValue = theoreticalFunction(sortedValues[i])

        val plusD = (i.toDouble() / n - theoreticalFunctionValue).absoluteValue
        val minusD = (theoreticalFunctionValue - (i - 1).toDouble() / n).absoluteValue
        maxD = if (plusD > maxD) plusD else maxD
        maxD = if (minusD > maxD) minusD else minusD
    }
    return maxD * sqrt(n.toDouble())
}