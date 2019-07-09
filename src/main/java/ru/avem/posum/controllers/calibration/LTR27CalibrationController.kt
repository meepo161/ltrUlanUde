package ru.avem.posum.controllers.calibration

import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.chart.LineChart
import javafx.scene.chart.XYChart
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.input.MouseButton
import org.controlsfx.control.StatusBar
import ru.avem.posum.ControllerManager
import ru.avem.posum.WindowsManager
import ru.avem.posum.controllers.BaseController
import ru.avem.posum.controllers.settings.LTR27.LTR27SettingsController
import ru.avem.posum.models.calibration.CalibrationPoint
import ru.avem.posum.models.calibration.LTR27CalibrationModel
import ru.avem.posum.utils.NewUtils
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
    private lateinit var calibrationOfChannelOneTableView: TableView<CalibrationPoint> // TODO: change this generic
    @FXML
    private lateinit var loadOfChannelOneColumn: TableColumn<CalibrationPoint, String>
    @FXML
    private lateinit var valueOfChannelOneColumn: TableColumn<CalibrationPoint, String>
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
    private lateinit var calibrationOfChannelTwoTableView: TableView<CalibrationPoint> // TODO: change this generic
    @FXML
    private lateinit var loadOfChannelTwoColumn: TableColumn<CalibrationPoint, String>
    @FXML
    private lateinit var valueOfChannelTwoColumn: TableColumn<CalibrationPoint, String>
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
    private lateinit var ltr27SettingsController: LTR27SettingsController
    private val ltr27CalibrationModel = LTR27CalibrationModel()
    private val lcm: LTR27CalibrationManager = ltr27CalibrationModel
    var submoduleIndex = 0
    private var showOfChannelOneStopped = false
    private var showOfChannelTwoStopped = false

    @FXML
    fun initialize() {
        statusBarLine = StatusBarLine(checkIcon, false, progressIndicator, statusBar, warningIcon)

        listen(valueOfChannelOneTextField, loadOfChannelOneTextField, valueNameOfChannelOneTextField, addToTableOfChannelOneButton)
        listen(valueOfChannelTwoTextField, loadOfChannelTwoTextField, valueNameOfChannelTwoTextField, addToTableOfChannelTwoButton)
        setMultipliers(valueOfChannelOneMultipliersComboBox)
        setMultipliers(loadOfChannelOneMultipliersComboBox)
        setMultipliers(valueOfChannelTwoMultipliersComboBox)
        setMultipliers(loadOfChannelTwoMultipliersComboBox)
        initTables()
        initGraph()
        initCheckBoxes()
    }

    private fun listen(valueOfChannel: TextField, loadOfChannel: TextField, valueName: TextField, button: Button) {
        setDigitFilterTo(valueOfChannel, loadOfChannel, valueName, button)
        setDigitFilterTo(loadOfChannel, valueOfChannel, valueName, button)

        valueName.textProperty().addListener { _ ->
            toggleStateOf(button, valueOfChannel, loadOfChannel, valueName)
        }
    }

    private fun setDigitFilterTo(textField: TextField, secondTextField: TextField, valueName: TextField, button: Button) {
        textField.textProperty().addListener { _, oldValue, newValue ->
            textField.text = newValue.replace("[^-\\d(\\.|,)]".toRegex(), "")
            if (!newValue.matches("^-?[\\d]+(\\.|,)\\d+|^-?[\\d]+(\\.|,)|^-?[\\d]+|-|$".toRegex())) {
                textField.text = oldValue
            }
            toggleStateOf(button, textField, secondTextField, valueName)
        }
    }

    private fun toggleStateOf(button: Button, firstTextField: TextField, secondTextField: TextField, valueName: TextField) {
        button.isDisable = firstTextField.text.isEmpty() || secondTextField.text.isEmpty() || valueName.text.isEmpty()
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

    private fun initTables() {
        loadOfChannelOneColumn.cellValueFactory = PropertyValueFactory<CalibrationPoint, String>("loadValue")
        valueOfChannelOneColumn.cellValueFactory = PropertyValueFactory<CalibrationPoint, String>("channelValue")
        loadOfChannelTwoColumn.cellValueFactory = PropertyValueFactory<CalibrationPoint, String>("loadValue")
        valueOfChannelTwoColumn.cellValueFactory = PropertyValueFactory<CalibrationPoint, String>("channelValue")
        calibrationOfChannelOneTableView.items = ltr27CalibrationModel.calibrationPointsOfChannelOne
        calibrationOfChannelTwoTableView.items = ltr27CalibrationModel.calibrationPointsOfChannelTwo

        val contextMenuOfChannelOne = getNewContextMenu(
                { deletePoint(calibrationOfChannelOneTableView, ltr27CalibrationModel.lineChartSeriesOfChannelOne) },
                { clearPoints(calibrationOfChannelOneTableView, ltr27CalibrationModel.lineChartSeriesOfChannelOne) })
        val contextMenuOfChannelTwo = getNewContextMenu(
                { deletePoint(calibrationOfChannelTwoTableView, ltr27CalibrationModel.lineChartSeriesOfChannelTwo) },
                { clearPoints(calibrationOfChannelTwoTableView, ltr27CalibrationModel.lineChartSeriesOfChannelTwo) })
        add(contextMenuOfChannelOne, calibrationOfChannelOneTableView)
        add(contextMenuOfChannelTwo, calibrationOfChannelTwoTableView)
    }

    private fun getNewContextMenu(deleteOperation: () -> Unit, clearOperation: () -> Unit): ContextMenu {
        val menuItemDelete = MenuItem("Удалить")
        val menuItemClear = MenuItem("Удалить все")

        menuItemDelete.setOnAction { deleteOperation() }
        menuItemClear.setOnAction { clearOperation() }

        return ContextMenu(menuItemDelete, menuItemClear)
    }

    private fun deletePoint(tableView: TableView<CalibrationPoint>, graphSeries: XYChart.Series<Number, Number>) {
        val selectedIndex = tableView.selectionModel.selectedIndex
        tableView.items.removeAt(selectedIndex)
        graphSeries.data.removeAt(selectedIndex)
    }

    private fun clearPoints(tableView: TableView<CalibrationPoint>, graphSeries: XYChart.Series<Number, Number>) {
        tableView.items.clear()
        graphSeries.data.clear()
    }

    private fun add(contextMenu: ContextMenu, tableView: TableView<CalibrationPoint>) {
        tableView.setRowFactory({ tv ->
            val row = TableRow<CalibrationPoint>()
            row.setOnMouseClicked { event ->
                if (event.button == MouseButton.SECONDARY && !row.isEmpty) {
                    contextMenu.show(tableView, event.screenX, event.screenY)
                } else if (event.clickCount == 1) {
                    contextMenu.hide()
                }
            }
            row
        })
    }

    private fun initGraph() {
        calibrationGraph.data.addAll(ltr27CalibrationModel.lineChartSeriesOfChannelOne, ltr27CalibrationModel.lineChartSeriesOfChannelTwo)
    }

    private fun initCheckBoxes() {
        listen(setValueOfChannelOneCheckBox)
        listen(setValueOfChannelTwoCheckBox)
    }

    private fun listen(checkBox: CheckBox) {
        checkBox.selectedProperty().addListener { _ ->
            if (!setValueOfChannelOneCheckBox.isSelected || !setValueOfChannelTwoCheckBox.isSelected) {
                if (showOfChannelOneStopped && showOfChannelTwoStopped) {
                    showValuesOfChannels()
                    toggleShowThreadState(setValueOfChannelOneCheckBox)
                    toggleShowThreadState(setValueOfChannelTwoCheckBox)
                }
            }

            toggleShowThreadState(checkBox)
        }
    }

    private fun toggleShowThreadState(checkBox: CheckBox) {
        val isChannelOneCheckBox = checkBox.id.contains("One")
        if (isChannelOneCheckBox) {
            showOfChannelOneStopped = checkBox.isSelected
            if (checkBox.isSelected) {
                Platform.runLater {
                    valueOfChannelOneTextField.text = ""
                    valueOfChannelOneTextField.isMouseTransparent = !checkBox.isSelected
                    valueOfChannelOneTextField.isFocusTraversable = checkBox.isSelected
                    valueOfChannelOneTextField.isEditable = checkBox.isSelected
                }
            }
        } else {
            showOfChannelTwoStopped = checkBox.isSelected
            if (checkBox.isSelected) {
                Platform.runLater {
                    valueOfChannelTwoTextField.text = ""
                    valueOfChannelTwoTextField.isMouseTransparent = !checkBox.isSelected
                    valueOfChannelTwoTextField.isFocusTraversable = checkBox.isSelected
                    valueOfChannelTwoTextField.isEditable = checkBox.isSelected
                }
            }
        }
    }

    fun setManagers() {
        ltr27SettingsController = cm.settingsController as LTR27SettingsController
        ltr27SettingsController.submoduleSettings.setLTR27CalibrationManager(lcm)
    }

    fun initView(title: String) {
        Platform.runLater { titleLabel.text = title }
        setValueName()
        showValuesOfChannels()
    }

    private fun setValueName() {
        val label = String.format("Значение, %s", ltr27SettingsController.submodulesDescriptions[submoduleIndex][2])
        Platform.runLater {
            valueOfChannelOneLabel.text = label
            valueOfChannelTwoLabel.text = label
        }

        val header = String.format("Значение на канале, %s", ltr27SettingsController.submodulesDescriptions[submoduleIndex][2])
        Platform.runLater {
            valueOfChannelOneColumn.text = header
            valueOfChannelTwoColumn.text = header
        }
    }

    private fun showValuesOfChannels() {
        showOfChannelOneStopped = false
        showOfChannelTwoStopped = false

        Thread {
            while (!cm.isClosed && !showOfChannelOneStopped || !showOfChannelTwoStopped) {
                if (!showOfChannelOneStopped) {
                    show(ltr27SettingsController.data[submoduleIndex * 2], valueOfChannelOneTextField)
                }
                if (!showOfChannelTwoStopped) {
                    show(ltr27SettingsController.data[submoduleIndex * 2 + 1], valueOfChannelTwoTextField)
                }
                sleep(200)
            }
        }.start()
    }

    private fun show(valueOfChannel: Double, textField: TextField) {
        Platform.runLater {
            val rarefactionCoefficient = ltr27SettingsController.rarefactionComboBox.selectionModel.selectedIndex + 1
            val channelValue = Utils.roundValue(valueOfChannel, Utils.getRounder(rarefactionCoefficient))
            textField.text = channelValue.toString()
        }
    }

    fun handleAddCalibrationPointOfChannelOne() {
        val calibrationPoint = parse(valueOfChannelOneTextField, valueOfChannelOneMultipliersComboBox,
                loadOfChannelOneTextField, loadOfChannelOneMultipliersComboBox, valueNameOfChannelOneTextField)
        calibrationPoint.channelNumber = 1
        ltr27CalibrationModel.calibrationPointsOfChannelOne.add(calibrationPoint)
        ltr27CalibrationModel.addPointToGraphOfChannelOne(calibrationPoint)
    }

    fun handleAddCalibrationPointOfChannelTwo() {
        val calibrationPoint = parse(valueOfChannelTwoTextField, valueOfChannelTwoMultipliersComboBox,
                loadOfChannelTwoTextField, loadOfChannelTwoMultipliersComboBox, valueNameOfChannelTwoTextField)
        calibrationPoint.channelNumber = 2
        ltr27CalibrationModel.calibrationPointsOfChannelTwo.add(calibrationPoint)
        ltr27CalibrationModel.addPointToGraphOfChannelTwo(calibrationPoint)
    }

    private fun parse(valueOfChannel: TextField, valueOfChannelMultipliers: ComboBox<String>,
                      loadOfChannel: TextField, loadOfChannelMultipliers: ComboBox<String>,
                      valueName: TextField): CalibrationPoint {
        val digits = ltr27SettingsController.rarefactionComboBox.selectionModel.selectedIndex + 1
        val channelValueMultiplier = valueOfChannelMultipliers.selectionModel.selectedItem.toDouble()
        val channelValue = valueOfChannel.text.replace(",", ".").toDouble()
        val convertedChannelValue = NewUtils.convertFromExponentialFormat(channelValue * channelValueMultiplier, digits)
        val loadValueMultiplier = loadOfChannelMultipliers.selectionModel.selectedItem.toDouble()
        val loadValue = loadOfChannel.text.replace(",", ".").toDouble()
        val convertedLoadValue = NewUtils.convertFromExponentialFormat(loadValue * loadValueMultiplier, digits)
        val name = valueName.text
        return CalibrationPoint(convertedLoadValue, convertedChannelValue, name)
    }

    fun handleSaveButton() {

    }

    fun handleBackButton() {
        showOfChannelOneStopped = true
        showOfChannelTwoStopped = true

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