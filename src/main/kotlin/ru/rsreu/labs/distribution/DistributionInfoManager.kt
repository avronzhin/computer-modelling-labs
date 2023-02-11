package ru.rsreu.labs.distribution

class DistributionInfoManager(private val values: List<Double>) {
    fun getEstimations() = values.getProbabilityDistributionEstimations()

    fun getDistributionFunctionsSeries(plotsNumber: Int): DistributionFunctionsSeries {
        val plots = MutableList(plotsNumber) { 0 }
        val plotStep = 1.0 / plotsNumber
        values.forEach {
            val plotNumber = (it / plotStep).toInt()
            plots[plotNumber]++
        }
        val probabilities = plots.map(Int::toDouble).map { it / values.size }
        val densityFunctionSeries = probabilities.map { it * plotsNumber }
        val distributionFunctionSeries = probabilities.runningReduce { prev, current -> prev + current }
        return DistributionFunctionsSeries(
            densityFunctionSeries,
            distributionFunctionSeries
        )
    }
}

data class DistributionFunctionsSeries(
    val densityFunctionSeries: List<Double>,
    val distributionFunctionSeries: List<Double>
)