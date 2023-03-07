package ru.rsreu.labs

import javafx.fxml.FXML
import ru.rsreu.labs.generator.MacLarenMarsagliaGenerator


class Controller {
    @FXML
    private fun initialize() {
    }

    @FXML
    private fun onStartClick() {
        val groups = mutableListOf(
            ShooterGroup(50, 0.4),
            ShooterGroup(200, 0.3),
            ShooterGroup(150, 0.2),
            ShooterGroup(50, 0.1),
            )

        val simulation = MonteCarloShooterSimulation(groups, MacLarenMarsagliaGenerator(64))
        simulation.start()
    }
}