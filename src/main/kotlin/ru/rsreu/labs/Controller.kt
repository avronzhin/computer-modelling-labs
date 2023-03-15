package ru.rsreu.labs

import javafx.fxml.FXML
import javafx.scene.control.TextArea
import javafx.scene.control.TextField


class Controller {
    @FXML
    private lateinit var resultTextArea: TextArea

    @FXML
    private lateinit var modelTimeTextField: TextField

    @FXML
    private lateinit var applicationMaxCountTextField: TextField

    @FXML
    private lateinit var revenueTextField: TextField

    @FXML
    private lateinit var serviceTimeTextField: TextField

    @FXML
    private lateinit var intensityTextField: TextField

    @FXML
    private lateinit var channelCountTextField: TextField

    @FXML
    private lateinit var channelCostTextField: TextField

    @FXML
    private fun onStartClick() {
        val system = createQueueingSystem()
        val applicationMaxCount = applicationMaxCountTextField.text.toInt()
        val modelTime = modelTimeTextField.text.toDouble()
        val (characteristicsEstimates, finance) = system.getModellingCharacteristicsEstimates(
            applicationMaxCount, modelTime
        )
        outputResult(characteristicsEstimates, finance)
    }

    private fun outputResult(
        characteristicsEstimates: QueueingSystem.CharacteristicsEstimates, finance: QueueingSystem.Finance
    ) {
        resultTextArea.text = StringBuilder().apply {
            appendLine("Количество заявок - ${characteristicsEstimates.applicationCount}")
            appendLine("Количество одобренных заявок - ${characteristicsEstimates.approvedCount}")
            appendLine("Вероятность отказа - ${characteristicsEstimates.failureProbability}")
            appendLine("Среднее время пребывания - ${characteristicsEstimates.averageStayTime}")
            appendLine("Коэффициент использования СМО - ${characteristicsEstimates.utilizationRate}")
            appendLine()
            appendLine("Доходы - ${finance.income}")
            appendLine("Расходы - ${finance.expenses}")
            appendLine("Прибыль - ${finance.profit}")
        }.toString()
    }

    private fun createQueueingSystem(): QueueingSystem {
        val channelCount = channelCountTextField.text.toInt()
        val channelCost = channelCostTextField.text.toDouble()
        val intensity = intensityTextField.text.toDouble()
        val serviceTime = serviceTimeTextField.text.toDouble()
        val revenue = revenueTextField.text.toDouble()

        return QueueingSystem(
            channelCount, channelCost, intensity, serviceTime, revenue
        )
    }
}

private fun Double.round(digits: Int): String {
    return "%.${digits}f".format(this)
}