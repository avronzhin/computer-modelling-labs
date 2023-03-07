package ru.rsreu.labs

import javafx.fxml.FXML
import javafx.scene.Group
import javafx.scene.chart.*
import javafx.scene.chart.XYChart.Series
import javafx.scene.control.Label
import javafx.scene.control.RadioButton
import javafx.scene.control.Spinner
import javafx.scene.control.ToggleGroup
import ru.rsreu.labs.distribution.DistributionCriterionInfo
import ru.rsreu.labs.distribution.DistributionInfoManager
import ru.rsreu.labs.distribution.DistributionParametersEstimations
import ru.rsreu.labs.generator.BoxMullerTransformGenerator
import ru.rsreu.labs.generator.CentralMarginalProfitGenerator
import ru.rsreu.labs.generator.Generator
import ru.rsreu.labs.generator.MacLarenMarsagliaGenerator
import java.lang.IllegalArgumentException
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
    @FXML
    private lateinit var radioButtonCMP: RadioButton
    @FXML
    private lateinit var radioButtonBoxMuller: RadioButton

    companion object {
        private const val EXPECTED_VALUE = 1.0
        private const val DISPERSION = 0.7
        private const val RANGE_START = EXPECTED_VALUE - 3 * DISPERSION
        private const val RANGE_END = EXPECTED_VALUE + 3 * DISPERSION
        private const val DIVISION_INCREMENT = 2
        private const val EPSILON = 1E-10
        fun createFunction(sectionsCount: Int): (Int) -> Double {
            val step = (RANGE_END - RANGE_START) / sectionsCount
            return { index: Int ->
                val sectionStart = RANGE_START + step * index
                var value = getSum(sectionStart, step)
                var divisionsCount: Long = DIVISION_INCREMENT.toLong()
                var prevValue: Double
                do {
                    prevValue = value
                    value = getSum(sectionStart, step, divisionsCount)
                    divisionsCount *= DIVISION_INCREMENT
                } while ((prevValue - value).absoluteValue > EPSILON)
                value
            }
        }

        private fun f(x: Double): Double {
            return exp(-1 * ((x - EXPECTED_VALUE).pow(2)) / (2 * DISPERSION)) /
                    (sqrt(DISPERSION) * sqrt(2 * PI))
        }

        private fun getSum(sectionStart: Double, globalStep: Double, divisionsCount: Long = 1): Double {
            val divisionSize = globalStep / divisionsCount
            var sum = 0.0
            for (i in 0 until divisionsCount) {
                val f1 = f(sectionStart + i * divisionSize)
                val f2 = f(sectionStart + (i + 1) * divisionSize)
                sum += divisionSize * (min(f1, f2) + abs(f1 - f2) / 2)
            }
            return sum
        }
    }

    @FXML
    private fun initialize() {
        val group = ToggleGroup()
        radioButtonCMP.toggleGroup = group
        radioButtonBoxMuller.toggleGroup = group
    }

    @FXML
    private fun onStartClick() {
        val n = nSpinner.value
        val k = kSpinner.value
        val sectionsCount = sectionsCountSpinner.value

        val uniformValueGenerator = MacLarenMarsagliaGenerator(k)
        val generator = getGenerator(uniformValueGenerator)

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

    private fun getGenerator(uniformValueGenerator: Generator): Generator {
        if(radioButtonCMP.isSelected)
            return CentralMarginalProfitGenerator(uniformValueGenerator, EXPECTED_VALUE, DISPERSION)
        if(radioButtonBoxMuller.isSelected)
            return BoxMullerTransformGenerator(uniformValueGenerator, EXPECTED_VALUE, DISPERSION)
        throw IllegalStateException()
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