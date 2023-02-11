package ru.rsreu.labs

import javafx.fxml.FXML
import javafx.scene.control.Label
import ru.rsreu.labs.distribution.DistributionInfoManager
import ru.rsreu.labs.generator.MacLarenMarsagliaGenerator

class HelloController {
    @FXML
    private lateinit var welcomeText: Label

    @FXML
    private fun onHelloButtonClick() {
        val generator = MacLarenMarsagliaGenerator(64)
        val values = MutableList(1000) { generator.nextDouble() }
        val manager = DistributionInfoManager(values)
        welcomeText.text = manager.getEstimations().expectedValue.toString()
    }
}