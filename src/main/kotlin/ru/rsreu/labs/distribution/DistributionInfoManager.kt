package ru.rsreu.labs.distribution

import ru.rsreu.labs.distribution.criterion.getChiSquaredCriterionValue
import ru.rsreu.labs.distribution.criterion.getKolmogorovCriterionValue
import java.lang.IllegalArgumentException
import java.util.function.Function
import java.util.function.UnaryOperator
import kotlin.math.pow
import kotlin.math.sqrt

class DistributionInfoManager(
    private val values: List<Double>,
    private val rangeStart: Double,
    private val rangeEnd: Double
) {
    fun getEstimations(): DistributionParametersEstimations {
        val moments = getMoments(3)
        val firstMoment = moments[0]
        val secondMoment = moments[1]
        val thirdMoment = moments[2]

        val dispersion = secondMoment - firstMoment.pow(2)
        return DistributionParametersEstimations(
            firstMoment,
            dispersion,
            secondMoment,
            thirdMoment
        )
    }

    private fun getMoments(count: Int): List<Double> {
        val momentSums = MutableList(count) { 0.0 }
        values.forEach { value ->
            for (i in 0 until count) {
                momentSums[i] += value.pow(i + 1)
            }
        }
        return momentSums.map { it / values.size }
    }

    fun getDistributionFunctionsSeries(plotsNumber: Int): DistributionFunctionsSeries {
        val sections = splitIntoSections(values, plotsNumber)
        val probabilities = getProbabilities(sections)
        return getDistributionFunctionsSeries(probabilities)
    }

    private fun getDistributionFunctionsSeries(probabilities: List<Double>): DistributionFunctionsSeries {
        val densityFunctionSeries = MutableList(probabilities.size) { 0.0 }
        val distributionFunctionSeries = MutableList(probabilities.size) { 0.0 }
        var acc = 0.0
        for (i in probabilities.indices) {
            val value = probabilities[i]
            densityFunctionSeries[i] = value
            acc += value
            distributionFunctionSeries[i] = acc
        }
        return DistributionFunctionsSeries(
            densityFunctionSeries,
            distributionFunctionSeries
        )
    }

    private fun getProbabilities(sections: List<Int>) = sections.map(Int::toDouble).map { it / values.size }

    fun getCriterionInfo(function: (Int) -> Double, k: Int): DistributionCriterionInfo {
        val chiSquaredCriterion =  getChiSquaredCriterionValue(values, k, rangeStart, rangeEnd, function)
        return DistributionCriterionInfo(chiSquaredCriterion)
    }

    private fun splitIntoSections(values: List<Double>, sectionsCount: Int): List<Int> {
        return splitIntoSections(values, sectionsCount, rangeStart, rangeEnd)
    }
}

fun splitIntoSections(values: List<Double>, sectionsCount: Int, rangeStart: Double, rangeEnd: Double): List<Int> {
    val plots = MutableList(sectionsCount) { 0 }
    val plotStep = (rangeEnd - rangeStart) / sectionsCount
    values.forEach {
        val plotNumber = ((it - rangeStart) / plotStep).toInt()
        if(plotNumber in 0 until sectionsCount)
            plots[plotNumber]++
    }
    return plots
}


data class DistributionCriterionInfo(
    val pearsonCriterion: Double
)

data class DistributionFunctionsSeries(
    val densityFunctionSeries: List<Double>,
    val distributionFunctionSeries: List<Double>
)

data class DistributionParametersEstimations(
    val expectedValue: Double,
    val dispersion: Double,
    val secondMoment: Double,
    val thirdMoment: Double
)