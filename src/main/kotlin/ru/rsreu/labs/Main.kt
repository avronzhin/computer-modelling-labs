package ru.rsreu.labs

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage

class ApplicationImpl : Application() {
    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(ApplicationImpl::class.java.getResource("view.fxml"))
        val scene = Scene(fxmlLoader.load())
        stage.scene = scene
        stage.width = 800.0
        stage.height = 500.0

        stage.show()
    }
}

fun main() {
    Application.launch(ApplicationImpl::class.java)
}