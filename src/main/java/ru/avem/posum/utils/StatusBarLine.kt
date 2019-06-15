package ru.avem.posum.utils

import javafx.application.Platform
import javafx.scene.control.Label
import javafx.scene.control.ProgressIndicator
import javafx.scene.paint.Color
import org.controlsfx.control.StatusBar

class StatusBarLine(private val checkIcon: Label, private val isMainView: Boolean,
                    private val progressIndicator: ProgressIndicator, private val statusBar: StatusBar,
                    private val warningIcon: Label) {
    private var statusBarThread = Thread()

    fun setStatusOfProgress(text: String) {
        Platform.runLater {
            clearStatusBar()
            toggleProgressIndicator(false)
            statusBar.style = "-fx-padding: 0 0 0 3.2;"
            statusBar.text = text
            handleStatusBar()
        }
    }

    private fun clearStatusBar() {
        statusBar.text = ""
        checkIcon.style = "-fx-opacity: 0;"
        warningIcon.style = "-fx-opacity: 0;"
    }

    private fun toggleIndicator(isHidden: Boolean) {
        if (isHidden) progressIndicator.style = "-fx-opacity: 0;"
        else progressIndicator.style = "-fx-opacity: 1;"
    }

    private fun handleStatusBar() {
        statusBarThread.interrupt()
        startNewStatusBarThread()
    }

    private fun startNewStatusBarThread() {
        statusBarThread = Thread {
            try {
                Thread.sleep(5000)
                clear()
            } catch (ignored: InterruptedException) {}
        }
        statusBarThread.start()
    }

    fun setStatus(text: String, isStatusSuccessful: Boolean) {
        Platform.runLater {
            clearStatusBar()
            toggleIndicator(true)
            statusBar.text = text
            initIcons(isStatusSuccessful)
            handleStatusBar()
        }
    }

    private fun initIcons(isStatusSuccessful: Boolean) {
        checkIcon.textFill = Color.web("#009700")
        warningIcon.textFill = Color.web("#D30303")
        val padding: String

        if (isStatusSuccessful) {
            checkIcon.style = "-fx-opacity: 1;"
            padding = if (isMainView) "-fx-padding: 0 0 0 4;" else "-fx-padding: 0 0 0 1.1;"
        } else {
            warningIcon.style = "-fx-opacity: 1;"
            padding = if (isMainView) "-fx-padding: 0 0 0 -1.1;" else "-fx-padding: 0 0 0 -1.9;"
        }

        statusBar.style = padding
    }

    fun toggleProgressIndicator(isHidden: Boolean) {
        Platform.runLater { toggleIndicator(isHidden) }
    }

    fun clear() {
        Platform.runLater { clearStatusBar() }
    }
}
