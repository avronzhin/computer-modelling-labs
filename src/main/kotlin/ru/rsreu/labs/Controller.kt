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
import ru.rsreu.labs.generator.BoxMullerTransformGenerator
import ru.rsreu.labs.generator.CentralMarginalProfitGenerator
import ru.rsreu.labs.generator.MacLarenMarsagliaGenerator
import kotlin.math.*


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
        private const val EXPECTED_VALUE = 1.0
        private const val DISPERSION = 0.7
        private const val RANGE_START = EXPECTED_VALUE - 3 * DISPERSION
        private const val RANGE_END = EXPECTED_VALUE + 3 * DISPERSION
        fun createFunction(sectionsCount: Int): (Int) -> Double {
            val step = (RANGE_END - RANGE_START) / sectionsCount

            fun f(x: Double): Double {
                return exp(-1 * ((x - EXPECTED_VALUE).pow(2)) / (2 * DISPERSION.pow(2))) /
                        (DISPERSION * sqrt(2 * PI))
            }

            return { index: Int ->
                val sectionStart = RANGE_START + step * index

                val f1 = f(sectionStart)
                val f2 = f(sectionStart + step)
                step * (min(f1, f2) + abs(f1 - f2) / 2)
            }
        }
    }


    @FXML
    private fun onStartClick() {
        val n = nSpinner.value
        val k = kSpinner.value
        val sectionsCount = sectionsCountSpinner.value

        val uniformValueGenerator = MacLarenMarsagliaGenerator(k)
        val generator = BoxMullerTransformGenerator(uniformValueGenerator, EXPECTED_VALUE, DISPERSION)
        val values = MutableList(n) { generator.nextDouble() }
        val manager = DistributionInfoManager(values, RANGE_START, RANGE_END)

        text.text = getEstimationsText(manager.getEstimations()) + "\n" + getCriterionInfoText(
            manager.getCriterionInfo(createFunction(sectionsCount), sectionsCount)
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
            "Критерий Пирсона ${pearsonCriterion.round(3)}"
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