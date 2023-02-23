package ru.rsreu.labs

import javafx.fxml.FXML
import javafx.scene.Group
import javafx.scene.chart.*
import javafx.scene.chart.XYChart.Series
import javafx.scene.control.Label
import javafx.scene.control.Spinner
import ru.rsreu.labs.distribution.DistributionCriterionInfo
import ru.rsreu.labs.distribution.DistributionInfoManager
import ru.rsreu.labs.distribution.DistributionParametersEstimations
import ru.rsreu.labs.generator.InverseTransformSamplingGenerator
import ru.rsreu.labs.generator.MacLarenMarsagliaGenerator
import kotlin.math.sqrt


class Controller {
    @FXML
    private lateinit var text: Label

    @FXML
    private lateinit var hist: Group

    @FXML
    private lateinit var function: Group

    @FXML
    private lateinit var kSpinner: Spinner<Int>

    @FXML
    private lateinit var nSpinner: Spinner<Int>

    @FXML
    private lateinit var sectionsCountSpinner: Spinner<Int>

    companion object {
        private const val RANGE_START = 0.0
        private const val RANGE_END = 2.25
        private val FUNCTION = { x: Double ->
            if (x < 0 || x > 2.25) throw IllegalArgumentException()
            if (x < 0.25) sqrt(x) else 0.25 * x + 0.4375
        }
    }


    @FXML
    private fun onStartClick() {
        val n = nSpinner.value
        val k = kSpinner.value
        val sectionsCount = sectionsCountSpinner.value

        val uniformValueGenerator = MacLarenMarsagliaGenerator(k)
        val generator = InverseTransformSamplingGenerator(uniformValueGenerator)
        val values = MutableList(n) { generator.nextDouble() }
        val manager = DistributionInfoManager(values, RANGE_START, RANGE_END)

        text.text = getEstimationsText(manager.getEstimations()) + "\n" + getCriterionInfoText(
            manager.getCriterionInfo(FUNCTION)
        )

        val series = manager.getDistributionFunctionsSeries(sectionsCount)
        val step = (RANGE_END - RANGE_START) / sectionsCount
        hist.children.clear()
        function.children.clear()
        hist.children.add(getHistogram(series.densityFunctionSeries, step, RANGE_START))
        function.children.add(getFunction(series.distributionFunctionSeries, step, RANGE_START))
    }

    private fun getCriterionInfoText(criterionInfo: DistributionCriterionInfo): String {
        return criterionInfo.run {
            "Критерий Колмогорова ${kolmogorovCriterionValue.round(3)}"
        }
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
        lineChart.title = "Функция распределения"
        val series = Series<Number, Number>()

        series.data.add(XYChart.Data(rangeStart, 0))

        var currentValue = step

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
        barChart.title = "Плотность распределения"
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