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
        clear()
        toggleProgressIndicator(false)
        Platform.runLater {
            statusBar.style = "-fx-padding: 0 0 0 3.2;"
            statusBar.text = text
            handleStatusBar()
        }
    }

    fun clear() {
        Platform.runLater {
            statusBar.text = ""
            hideIcons()
        }
    }

    private fun hideIcons() {
        checkIcon.style = "-fx-opacity: 0;"
        warningIcon.style = "-fx-opacity: 0;"
    }

    fun toggleProgressIndicator(isHidden: Boolean) {
        Platform.runLater {
            if (isHidden) progressIndicator.style = "-fx-opacity: 0;"
            else progressIndicator.style = "-fx-opacity: 1;"
        }
    }

    private fun handleStatusBar() {
        statusBarThread.interrupt()
        startNewStatusBarThread()
    }

    private fun startNewStatusBarThread() {
        statusBarThread = Thread {
            Utils.sleep(5000)
            clear()
        }
        statusBarThread.start()
    }

    fun setStatus(text: String, isStatusSuccessful: Boolean) {
        clear()
        toggleProgressIndicator(true)
        Platform.runLater {
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
}
