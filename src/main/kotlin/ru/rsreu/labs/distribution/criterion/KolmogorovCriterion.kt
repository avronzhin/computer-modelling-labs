package ru.rsreu.labs.distribution.criterion

import java.util.function.UnaryOperator
import kotlin.math.absoluteValue
import kotlin.math.sqrt

fun getKolmogorovCriterionValue(values: List<Double>, theoreticalFunction: UnaryOperator<Double>): Double {
    var maxD = Double.MIN_VALUE
    val sortedValues = values.sorted()

    val n = values.size

    for (i in values.indices) {
        val theoreticalFunctionValue = theoreticalFunction.apply(sortedValues[i])
        val plusD = (i.toDouble() / n - theoreticalFunctionValue).absoluteValue
        val minusD = (theoreticalFunctionValue - (i - 1).toDouble() / n).absoluteValue
        if (plusD > maxD) maxD = plusD
        if (minusD > maxD) maxD = minusD
    }
    return maxD * sqrt(n.toDouble())
}