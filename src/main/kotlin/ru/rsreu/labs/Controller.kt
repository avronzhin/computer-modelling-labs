package ru.rsreu.labs

import javafx.fxml.FXML
import javafx.scene.control.Spinner
import javafx.scene.control.TextField
import javafx.scene.layout.FlowPane
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import ru.rsreu.labs.generator.MacLarenMarsagliaGenerator


class Controller {
    companion object {
        private const val MONTE_CARLO_ITERATION_COUNT = 100000
        private const val CALCULATION_COUNT = 10
        private const val T_CRITERION = 2.2621572
        private const val DOUBLE_VALUE_DIGITS = 5
    }

    @FXML
    private lateinit var resultFlowPane: FlowPane

    @FXML
    private lateinit var groupsFlowPane: FlowPane

    @FXML
    private lateinit var groupCountSpinner: Spinner<Int>

    @FXML
    private fun initialize() {
        generateDefaultGroups()
    }

    private fun generateDefaultGroups() {
        val defaultGroups = listOf(
            ShooterGroup(50, 0.4),
            ShooterGroup(200, 0.3),
            ShooterGroup(150, 0.2),
            ShooterGroup(50, 0.1),
        )
        createGroups(defaultGroups)
    }

    private fun createGroups(groups: List<ShooterGroup>) {
        groupsFlowPane.children.apply {
            clear()
            groups.forEachIndexed { index, group ->
                val vbox = VBox()
                vbox.prefWidth = 100.0
                vbox.children.addAll(
                    Text(index.toString()),
                    TextField(group.shooterCount.toString()),
                    TextField(group.hitProbability.toString())
                )
                add(vbox)
            }
        }
    }

    @FXML
    private fun onStartClick() {
        val groups = extractGroups()
        val simulation = MonteCarloShooterSimulation(groups, MacLarenMarsagliaGenerator(), MONTE_CARLO_ITERATION_COUNT)
        val values = MutableList(CALCULATION_COUNT) { simulation.calculate() }
        val confidenceInterval = getConfidenceInterval(values, T_CRITERION)
        val analyticalResult = AnalyticalSolutionShooterSimulation(groups).calculate()
        resultFlowPane.children.apply {
            clear()
            add(Text(prepareResultString(confidenceInterval, analyticalResult, values)))
        }
    }

    private fun extractGroups(): List<ShooterGroup> {
        return groupsFlowPane.children.map {
            val vbox = it as VBox
            val shooterCount = (vbox.children[1] as TextField).text.toInt()
            val hitProbability = (vbox.children[2] as TextField).text.toDouble()
            ShooterGroup(shooterCount, hitProbability)
        }
    }

    private fun prepareResultString(
        result: ConfidenceInterval, analyticalResult: Double, values: List<Double>
    ): String {
        return "Вероятность хотя бы одного попадания в мишень:\n\n" +
                "Вероятность рассчитанная аналитически ${analyticalResult.round(DOUBLE_VALUE_DIGITS)}\n\n" +
                "Результаты методом Монте-Карло:\n" + values.joinToString("\n") { it.round(DOUBLE_VALUE_DIGITS) } + "\n" +
                "Доверительный интервал [${result.intervalStart.round(DOUBLE_VALUE_DIGITS)}, ${result.intervalFinish.round(DOUBLE_VALUE_DIGITS)}]."
    }
}

private fun Double.round(digits: Int): String {
    return "%.${digits}f".format(this)
}