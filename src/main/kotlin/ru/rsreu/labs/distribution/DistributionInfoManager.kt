package ru.rsreu.labs.distribution

import kotlin.math.pow

class DistributionInfoManager(private val values: List<Double>) {
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

    private fun getMoments(count: Int) : List<Double>{
        val momentSums = MutableList(count) {0.0}
        values.forEach { value ->
            for(i in 0 until  count){
                momentSums[i] += value.pow(i + 1)
            }
        }
        return momentSums.map{ it / values.size }
    }

    fun getDistributionFunctionsSeries(plotsNumber: Int): DistributionFunctionsSeries {
        val plots = getPlots(plotsNumber)
        val probabilities = getProbabilities(plots)
        return getDistributionFunctionsSeries(probabilities)
    }

    private fun getPlots(count: Int): MutableList<Int> {
        val plots = MutableList(count) { 0 }
        val plotStep = 1.0 / count
        values.forEach {
            val plotNumber = (it / plotStep).toInt()
            plots[plotNumber]++
        }
        return plots
    }

    private fun getDistributionFunctionsSeries(probabilities: List<Double>): DistributionFunctionsSeries {
        val densityFunctionSeries = MutableList(probabilities.size) { 0.0 }
        val distributionFunctionSeries = MutableList(probabilities.size) { 0.0 }
        var acc = 0.0
        for(i in probabilities.indices){
            val value = probabilities[i]
            densityFunctionSeries[i] = value * probabilities.size
            acc += value
            distributionFunctionSeries[i] = acc
        }
        return DistributionFunctionsSeries(
            densityFunctionSeries,
            distributionFunctionSeries
        )
    }

    private fun getProbabilities(plots: List<Int>) = plots.map(Int::toDouble).map { it / values.size }
}

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