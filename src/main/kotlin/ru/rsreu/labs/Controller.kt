package ru.rsreu.labs

import javafx.fxml.FXML


class Controller {

    @FXML
    private fun onStartClick() {
        val system = QueueingSystem(
            channelCount = 2, channelCost = 3.0, intensity = 3.0, serviceTime = 0.5, revenue = 5.0
        )
        val estimates = system.getCharacteristicsEstimates()
        println(estimates)
    }
}

private fun Double.round(digits: Int): String {
    return "%.${digits}f".format(this)
}