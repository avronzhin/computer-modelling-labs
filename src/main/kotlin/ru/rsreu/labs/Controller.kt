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

    @FXML
    private fun onStartClick() {
        val groups = extractGroups()
        val simulation = MonteCarloShooterSimulation(groups, MacLarenMarsagliaGenerator(), MONTE_CARLO_ITERATION_COUNT)
        val values = MutableList(CALCULATION_COUNT) { simulation.getProbability() }
        val confidenceInterval = getConfidenceInterval(values, T_CRITERION)
        val analyticalResult = simulation.getAnalyticalSolutionProbability()
        resultFlowPane.children.apply {
            clear()
            add(Text(prepareResultString(confidenceInterval, analyticalResult)))
        }
    }

    private fun prepareResultString(result: ConfidenceInterval, analyticalResult: Double): String {
        return """
            Вероятность хотя бы одного попадания в мишень:
            Вероятность рассчитанная аналитическа $analyticalResult
            Доверительный интервал $result
        """.trimIndent()
    }

    private fun extractGroups(): List<ShooterGroup> {
        return groupsFlowPane.children.map {
            val vbox = it as VBox
            val shooterCount = (vbox.children[1] as TextField).text.toInt()
            val hitProbability = (vbox.children[2] as TextField).text.toDouble()
            ShooterGroup(shooterCount, hitProbability)
        }
    }

    @FXML
    private fun createEmptyGroups() {
        val groupCount = groupCountSpinner.value
        val emptyGroups = (0 until groupCount).map { ShooterGroup(100, 1.0 / groupCount) }
        createGroups(emptyGroups)
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
}