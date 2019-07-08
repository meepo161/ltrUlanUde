package ru.avem.posum.controllers.calibration

import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.chart.LineChart
import javafx.scene.control.*
import org.controlsfx.control.StatusBar
import ru.avem.posum.ControllerManager
import ru.avem.posum.WindowsManager
import ru.avem.posum.controllers.BaseController
import ru.avem.posum.utils.StatusBarLine

class LTR27CalibrationController: BaseController {
    @FXML
    private lateinit var titleLabel: Label
    @FXML
    private lateinit var valueOfChannelOneLabel: Label
    @FXML
    private lateinit var valueOfChannelOneTextField: TextField
    @FXML
    private lateinit var valueOfChannelOneCoefficientLabel: Label
    @FXML
    private lateinit var valueOfChannelOneMultipliersComboBox: ComboBox<String>
    @FXML
    private lateinit var loadOfChannelOneLabel: Label
    @FXML
    private lateinit var loadOfChannelOneTextField: TextField
    @FXML
    private lateinit var loadOfChannelOneCoefficientLabel: Label
    @FXML
    private lateinit var loadOfChannelOneMultipliersComboBox: ComboBox<String>
    @FXML
    private lateinit var valueNameOfChannelOneLabel: Label
    @FXML
    private lateinit var valueNameOfChannelOneTextField: TextField
    @FXML
    private lateinit var setValueOfChannelOneCheckBox: CheckBox
    @FXML
    private lateinit var setNulOfChannelOneCheckBox: CheckBox
    @FXML
    private lateinit var addToTableOfChannelOneButton: Button
    @FXML
    private lateinit var calibrationOfChannelOneTableView: TableView<String> // TODO: change this generic
    @FXML
    private lateinit var valueOfChannelTwoLabel: Label
    @FXML
    private lateinit var valueOfChannelTwoTextField: TextField
    @FXML
    private lateinit var valueOfChannelTwoCoefficientLabel: Label
    @FXML
    private lateinit var valueOfChannelTwoMultipliersComboBox: ComboBox<String>
    @FXML
    private lateinit var loadOfChannelTwoLabel: Label
    @FXML
    private lateinit var loadOfChannelTwoTextField: TextField
    @FXML
    private lateinit var loadOfChannelTwoCoefficientLabel: Label
    @FXML
    private lateinit var loadOfChannelTwoMultipliersComboBox: ComboBox<String>
    @FXML
    private lateinit var valueNameOfChannelTwoLabel: Label
    @FXML
    private lateinit var valueNameOfChannelTwoTextField: TextField
    @FXML
    private lateinit var setValueOfChannelTwoCheckBox: CheckBox
    @FXML
    private lateinit var setNulOfChannelTwoCheckBox: CheckBox
    @FXML
    private lateinit var addToTableOfChannelTwoButton: Button
    @FXML
    private lateinit var calibrationOfChannelTwoTableView: TableView<String> // TODO: change this generic
    @FXML
    private lateinit var calibrationGraph: LineChart<Number, Number>
    @FXML
    private lateinit var saveButton: Button
    @FXML
    private lateinit var backButton: Button
    @FXML
    private lateinit var progressIndicator: ProgressIndicator
    @FXML
    private lateinit var statusBar: StatusBar
    @FXML
    private lateinit var warningIcon: Label
    @FXML
    private lateinit var checkIcon: Label

    private lateinit var cm: ControllerManager
    private lateinit var wm: WindowsManager
    private lateinit var statusBarLine: StatusBarLine

    @FXML
    fun initialize() {
        statusBarLine = StatusBarLine(checkIcon, false, progressIndicator, statusBar, warningIcon)
    }

    fun setTitle(title: String) {
        Platform.runLater { titleLabel.text = title }
    }

    fun handleAddPoint() {

    }

    fun handleSaveButton() {

    }

    fun handleBackButton() {
        Platform.runLater { wm.setScene(WindowsManager.Scenes.LTR27_SCENE) }
    }

    override fun setControllerManager(cm: ControllerManager) {
        this.cm = cm
    }

    override fun setWindowManager(wm: WindowsManager) {
        this.wm = wm
    }
}