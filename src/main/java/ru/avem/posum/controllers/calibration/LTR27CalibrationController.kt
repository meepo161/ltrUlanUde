package ru.avem.posum.controllers.calibration

import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.chart.LineChart
import javafx.scene.control.*
import org.controlsfx.control.StatusBar
import ru.avem.posum.ControllerManager
import ru.avem.posum.WindowsManager
import ru.avem.posum.controllers.BaseController
import ru.avem.posum.utils.StatusBarLine
import ru.avem.posum.utils.Utils
import java.lang.Thread.sleep

class LTR27CalibrationController : BaseController {
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
    var submoduleIndex = 0
    private var stopped = false

    @FXML
    fun initialize() {
        statusBarLine = StatusBarLine(checkIcon, false, progressIndicator, statusBar, warningIcon)

        setMultipliers(valueOfChannelOneMultipliersComboBox)
        setMultipliers(loadOfChannelOneMultipliersComboBox)
        setMultipliers(valueOfChannelTwoMultipliersComboBox)
        setMultipliers(loadOfChannelTwoMultipliersComboBox)
    }

    private fun setMultipliers(comboBox: ComboBox<String>) {
        val multipliers = FXCollections.observableArrayList<String>()
        multipliers.add("0.00001")
        multipliers.add("0.0001")
        multipliers.add("0.001")
        multipliers.add("0.01")
        multipliers.add("0.1")
        multipliers.add("1")
        multipliers.add("10")
        multipliers.add("100")
        multipliers.add("1000")
        multipliers.add("10000")
        multipliers.add("100000")
        comboBox.items = multipliers
        comboBox.selectionModel.select(5)
    }

    fun setTitle(title: String) {
        Platform.runLater { titleLabel.text = title }
    }

    fun showValuesOfChannels() {
        stopped = false

        Thread {
            while (!stopped) {
                Platform.runLater {
                    val rarefactionCoefficient = cm.ltr27Settings.rarefactionComboBox.selectionModel.selectedIndex + 1
                    val channelOneValue = Utils.roundValue(cm.ltr27Settings.data[submoduleIndex * 2], Utils.getRounder(rarefactionCoefficient))
                    val channelTwoValue = Utils.roundValue(cm.ltr27Settings.data[submoduleIndex * 2 + 1], Utils.getRounder(rarefactionCoefficient))
                    valueOfChannelOneTextField.text = channelOneValue.toString()
                    valueOfChannelTwoTextField.text = channelTwoValue.toString()
                }
                sleep(1000)
            }
        }.start()
    }

    fun handleAddPoint() {

    }

    fun handleSaveButton() {

    }

    fun handleBackButton() {
        val moduleName = cm.hardwareSettings.moduleName
        val selectedModuleIndex = cm.hardwareSettings.selectedModuleIndex
        wm.setModuleScene(moduleName, selectedModuleIndex)
    }

    override fun setControllerManager(cm: ControllerManager) {
        this.cm = cm
    }

    override fun setWindowManager(wm: WindowsManager) {
        this.wm = wm
    }
}