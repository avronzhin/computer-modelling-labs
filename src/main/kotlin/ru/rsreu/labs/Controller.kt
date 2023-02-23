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
import ru.rsreu.labs.generator.MacLarenMarsagliaGenerator


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
    private fun onStartClick() {
        val n = nSpinner.value
        val k = kSpinner.value
        val sectionsCount = sectionsCountSpinner.value

        val generator = MacLarenMarsagliaGenerator(k)
        val values = MutableList(n) { generator.nextDouble() }
        val manager = DistributionInfoManager(values)
        text.text = getEstimationsText(manager.getEstimations()) + "\n" + getCriterionInfoText(
            manager.getCriterionInfo(sectionsCount)
        )

        val series = manager.getDistributionFunctionsSeries(sectionsCount)
        val step = 1.0 / sectionsCount
        hist.children.clear()
        function.children.clear()
        hist.children.add(getHistogram(series.densityFunctionSeries, step))
        function.children.add(getFunction(series.distributionFunctionSeries, step))
    }

    private fun getCriterionInfoText(criterionInfo: DistributionCriterionInfo): String {
        return criterionInfo.run {
            "Критерий Пирсона ${chiSquaredCriterionValue.round(3)}, " + "критерий Колмогорова ${
                kolmogorovCriterionValue.round(
                    3
                )
            }, " + "критерий серии нулей vH = ${runCriterionInfo.vH.round(3)} M = ${runCriterionInfo.value.round(3)} " + "vB = ${
                runCriterionInfo.vB.round(
                    3
                )
            }"
        }
    }

    private fun getEstimationsText(estimations: DistributionParametersEstimations): String {
        return estimations.run {
            "Математическое ожидание ${expectedValue.round(3)}, " + "дисперсия ${dispersion.round(3)}, " + "2 момент ${
                secondMoment.round(
                    3
                )
            }, " + "3 момент ${thirdMoment.round(3)}"
        }
    }

    private fun Double.round(digits: Int): String {
        return "%.${digits}f".format(this)
    }

    private fun getFunction(distributionFunctionSeries: List<Double>, step: Double): LineChart<Number, Number> {
        val lineChart = LineChart(NumberAxis(), NumberAxis())
        lineChart.title = "Функция распределения"
        val series = Series<Number, Number>()

        series.data.add(XYChart.Data(0, 0))

        var currentValue = step

        distributionFunctionSeries.forEach {
            series.data.add(XYChart.Data(currentValue, it))
            currentValue += step
        }

        lineChart.data.add(series)
        return lineChart
    }

    private fun getHistogram(densityFunctionSeries: List<Double>, step: Double): StackedBarChart<String, Number> {
        val barChart = StackedBarChart(CategoryAxis(), NumberAxis())
        barChart.title = "Плотность распределения"
        val series = Series<String, Number>()
        var currentValue = 0.0
        densityFunctionSeries.forEach {
            series.data.add(XYChart.Data(String.format("%.2f", currentValue), it))
            currentValue += step
        }
        barChart.data.add(series)
        return barChart
    }
}