package ru.rsreu.labs

import javafx.fxml.FXML
import javafx.scene.Group
import javafx.scene.chart.*
import javafx.scene.chart.XYChart.Series
import javafx.scene.control.Label
import javafx.scene.control.Spinner
import javafx.scene.control.TextField
import ru.rsreu.labs.distribution.DistributionInfoManager
import ru.rsreu.labs.distribution.DistributionParametersEstimations
import ru.rsreu.labs.generator.MacLarenMarsagliaGenerator


class Controller {
    @FXML
    private lateinit var leftPTextField: TextField

    @FXML
    private lateinit var mSpinner: Spinner<Int>

    @FXML
    private lateinit var text: Label

    @FXML
    private lateinit var hist: Group

    @FXML
    private lateinit var function: Group

    @FXML
    private lateinit var nSpinner: Spinner<Int>

    @FXML
    private lateinit var sectionsCountSpinner: Spinner<Int>

    companion object {
        private const val RANGE_START = 0.0
        private const val RANGE_END = 20.0
    }

    @FXML
    private fun onStartClick() {
        val n = nSpinner.value
        val m = mSpinner.value
        val leftP = leftPTextField.text.toDouble()

        val randomWalk = RandomWalk(m, MacLarenMarsagliaGenerator(), leftP)
        val values = MutableList(n) { randomWalk.getValue().toDouble() }

        outputDistribution(values)
    }

    private fun outputDistribution(values: List<Double>) {
        val sectionsCount = sectionsCountSpinner.value
        val manager = DistributionInfoManager(values, RANGE_START, RANGE_END)
        text.text = getEstimationsText(manager.getEstimations())
        val series = manager.getDistributionFunctionsSeries(sectionsCount)
        val step = (RANGE_END - RANGE_START) / sectionsCount
        hist.children.clear()
        function.children.clear()
        hist.children.add(getHistogram(series.densityFunctionSeries, step, RANGE_START))
        function.children.add(getFunction(series.distributionFunctionSeries, step, RANGE_START))
    }

    private fun getEstimationsText(estimations: DistributionParametersEstimations): String {
        return estimations.run {
            "Математическое ожидание ${expectedValue.round(3)}, " + "дисперсия ${dispersion.round(3)}"
        }
    }

    private fun Double.round(digits: Int): String {
        return "%.${digits}f".format(this)
    }

    private fun getFunction(
        distributionFunctionSeries: List<Double>, step: Double, rangeStart: Double
    ): LineChart<Number, Number> {
        val lineChart = LineChart(NumberAxis(), NumberAxis())
        lineChart.title = "Статистическая плотность распределения"
        val series = Series<Number, Number>()

        series.data.add(XYChart.Data(rangeStart, 0))

        var currentValue = rangeStart + step

        distributionFunctionSeries.forEach {
            series.data.add(XYChart.Data(currentValue, it))
            currentValue += step
        }

        lineChart.data.add(series)
        return lineChart
    }

    private fun getHistogram(
        densityFunctionSeries: List<Double>, step: Double, rangeStart: Double
    ): StackedBarChart<String, Number> {
        val barChart = StackedBarChart(CategoryAxis(), NumberAxis())
        barChart.title = "Гистограмма частот"
        val series = Series<String, Number>()
        var currentValue = rangeStart
        densityFunctionSeries.forEach {
            series.data.add(XYChart.Data(String.format("%.2f", currentValue), it))
            currentValue += step
        }
        barChart.data.add(series)
        return barChart
    }
}