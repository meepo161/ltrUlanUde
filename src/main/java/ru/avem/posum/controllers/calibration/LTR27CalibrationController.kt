package ru.avem.posum.controllers.calibration

import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.ObservableList
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

class LTR27CalibrationController : BaseController, LTR27CalibrationManager {
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
    private var submoduleIndex = 0
    private var showOfChannelOneStopped = false
    private var showOfChannelTwoStopped = false

    @FXML
    fun initialize() {
        statusBarLine = StatusBarLine(checkIcon, false, progressIndicator, statusBar, warningIcon)

        listen(calibrationOfChannelOneTableView, valueOfChannelOneTextField, loadOfChannelOneTextField,
                valueNameOfChannelOneTextField, addToTableOfChannelOneButton)
        listen(calibrationOfChannelTwoTableView,valueOfChannelTwoTextField, loadOfChannelTwoTextField,
                valueNameOfChannelTwoTextField, addToTableOfChannelTwoButton)
        setMultipliers(valueOfChannelOneMultipliersComboBox)
        setMultipliers(loadOfChannelOneMultipliersComboBox)
        setMultipliers(valueOfChannelTwoMultipliersComboBox)
        setMultipliers(loadOfChannelTwoMultipliersComboBox)
        initTables()
        initGraph()
        initCheckBoxes()
    }

    private fun listen(tableView: TableView<CalibrationPoint>, valueOfChannel: TextField, loadOfChannel: TextField, valueName: TextField, button: Button) {
        setDigitFilterTo(tableView, valueOfChannel, loadOfChannel, valueName, button)
        setDigitFilterTo(tableView, loadOfChannel, valueOfChannel, valueName, button)

        valueName.textProperty().addListener { _ ->
            checkPointsCount(tableView.items, button, valueOfChannel, loadOfChannel, valueName)
        }
    }

    private fun setDigitFilterTo(tableView: TableView<CalibrationPoint>, textField: TextField, secondTextField: TextField, valueName: TextField, button: Button) {
        textField.textProperty().addListener { _, oldValue, newValue ->
            textField.text = newValue.replace("[^-\\d(\\.|,)]".toRegex(), "")
            if (!newValue.matches("^-?[\\d]+(\\.|,)\\d+|^-?[\\d]+(\\.|,)|^-?[\\d]+|-|$".toRegex())) {
                textField.text = oldValue
            }
            checkPointsCount(tableView.items, button, textField, secondTextField, valueName)
        }
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
    }

    private fun loadTablesSettings() {
        calibrationOfChannelOneTableView.items = ltr27CalibrationModel.bufferedCalibrationPointsOfChannelOne[submoduleIndex]
        calibrationOfChannelTwoTableView.items = ltr27CalibrationModel.bufferedCalibrationPointsOfChannelTwo[submoduleIndex]

        val contextMenuOfChannelOne = getNewContextMenu(
                { deletePoint(calibrationOfChannelOneTableView, ltr27CalibrationModel.lineChartSeriesOfChannelOne, valueNameOfChannelOneTextField) },
                { clearPoints(calibrationOfChannelOneTableView, ltr27CalibrationModel.lineChartSeriesOfChannelOne, valueNameOfChannelOneTextField) })
        val contextMenuOfChannelTwo = getNewContextMenu(
                { deletePoint(calibrationOfChannelTwoTableView, ltr27CalibrationModel.lineChartSeriesOfChannelTwo, valueNameOfChannelTwoTextField) },
                { clearPoints(calibrationOfChannelTwoTableView, ltr27CalibrationModel.lineChartSeriesOfChannelTwo, valueNameOfChannelTwoTextField) })
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

    private fun deletePoint(tableView: TableView<CalibrationPoint>, graphSeries: XYChart.Series<Number, Number>, textField: TextField) {
        val selectedIndex = tableView.selectionModel.selectedIndex
        tableView.items.removeAt(selectedIndex)
        graphSeries.data.removeAt(selectedIndex)
        checkValueName(tableView.items, textField, tableView.columns[0] as TableColumn<CalibrationPoint, String>)
    }

    private fun clearPoints(tableView: TableView<CalibrationPoint>, graphSeries: XYChart.Series<Number, Number>, textField: TextField) {
        tableView.items.clear()
        graphSeries.data.clear()
        checkValueName(tableView.items, textField, tableView.columns[0] as TableColumn<CalibrationPoint, String>)
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
        listen(setValueOfChannelOneCheckBox, calibrationOfChannelOneTableView, addToTableOfChannelOneButton, valueOfChannelOneTextField,
                loadOfChannelOneTextField, valueNameOfChannelOneTextField)
        listen(setValueOfChannelTwoCheckBox, calibrationOfChannelTwoTableView, addToTableOfChannelTwoButton, valueOfChannelTwoTextField,
                loadOfChannelTwoTextField, valueNameOfChannelTwoTextField)
    }

    private fun listen(checkBox: CheckBox, tableView: TableView<CalibrationPoint>, button: Button, valueOfChannel: TextField,
                       loadOfChannel: TextField, valueName: TextField) {
        checkBox.selectedProperty().addListener { _ ->
            if (!setValueOfChannelOneCheckBox.isSelected || !setValueOfChannelTwoCheckBox.isSelected) {
                if (showOfChannelOneStopped && showOfChannelTwoStopped) {
                    showValuesOfChannels()
                    toggleShowThreadState(setValueOfChannelOneCheckBox)
                    toggleShowThreadState(setValueOfChannelTwoCheckBox)
                }
            }

            toggleShowThreadState(checkBox)
            valueOfChannel.isEditable = checkBox.isSelected
            valueOfChannel.isMouseTransparent = !checkBox.isSelected
            valueOfChannel.isFocusTraversable = !checkBox.isSelected
            checkPointsCount(tableView.items, button, valueOfChannel, loadOfChannel, valueName)
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
        ltr27SettingsController.setLTR27CalibrationManager(this)
        ltr27SettingsController.submoduleSettings.setLTR27CalibrationManager(this)
    }

    override fun initCalibrationView(title: String, submoduleIndex: Int) {
        Platform.runLater { titleLabel.text = title }
        this.submoduleIndex = submoduleIndex
        ltr27CalibrationModel.load(submoduleIndex)
        ltr27CalibrationModel.updateGraph(submoduleIndex)
        loadTablesSettings()
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
            while (!cm.isClosed && (!showOfChannelOneStopped || !showOfChannelTwoStopped)) {
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
        calibrationPoint.channelNumber = submoduleIndex
        val calibrationPoints = ltr27CalibrationModel.bufferedCalibrationPointsOfChannelOne[submoduleIndex]
        calibrationPoints.add(calibrationPoint)
        ltr27CalibrationModel.addPointToGraphOfChannelOne(calibrationPoint)
        checkValueName(calibrationPoints, valueNameOfChannelOneTextField, loadOfChannelOneColumn)
        checkPointsCount(calibrationPoints, addToTableOfChannelOneButton, valueOfChannelOneTextField, loadOfChannelOneTextField, valueNameOfChannelOneTextField)
    }

    private fun checkValueName(calibrationsPoints: List<CalibrationPoint>, textField: TextField, column: TableColumn<CalibrationPoint, String>) {
        Platform.runLater {
            textField.isDisable = calibrationsPoints.isNotEmpty()
            if (calibrationsPoints.isNotEmpty()) {
                column.text = "Величина нагрузки, ${calibrationsPoints.first().valueName}"
            } else {
                column.text = "Величина нагрузки"
            }
        }
    }

    private fun checkPointsCount(calibrationPoints: List<CalibrationPoint>, button: Button, valueOfChannel: TextField, loadOfChannel: TextField, valueName: TextField) {
        button.isDisable = calibrationPoints.size >= 2 || valueOfChannel.text.isEmpty() || loadOfChannel.text.isEmpty() || valueName.text.isEmpty()
    }

    fun handleAddCalibrationPointOfChannelTwo() {
        val calibrationPoint = parse(valueOfChannelTwoTextField, valueOfChannelTwoMultipliersComboBox,
                loadOfChannelTwoTextField, loadOfChannelTwoMultipliersComboBox, valueNameOfChannelTwoTextField)
        calibrationPoint.channelNumber = submoduleIndex + 1
        val calibrationPoints = ltr27CalibrationModel.bufferedCalibrationPointsOfChannelTwo[submoduleIndex]
        calibrationPoints.add(calibrationPoint)
        ltr27CalibrationModel.addPointToGraphOfChannelTwo(calibrationPoint)
        checkValueName(calibrationPoints, valueNameOfChannelTwoTextField, loadOfChannelTwoColumn)
        checkPointsCount(calibrationPoints, addToTableOfChannelTwoButton, valueOfChannelTwoTextField, loadOfChannelTwoTextField, valueNameOfChannelTwoTextField)
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
        ltr27CalibrationModel.save(submoduleIndex)
    }

    fun handleBackButton() {
        statusBarLine.setStatusOfProgress("Загрузка настроек модуля")

        Thread {
            showOfChannelOneStopped = true
            showOfChannelTwoStopped = true
            clearView()
            Platform.runLater {
                val moduleName = cm.hardwareSettings.moduleName
                val selectedModuleIndex = cm.hardwareSettings.selectedModuleIndex
                wm.setModuleScene(moduleName, selectedModuleIndex)
            }
        }.start()
    }

    private fun clearView() {
        ltr27CalibrationModel.clearBuffer(submoduleIndex)
        Platform.runLater {
            ltr27CalibrationModel.lineChartSeriesOfChannelOne.data.clear()
            ltr27CalibrationModel.lineChartSeriesOfChannelTwo.data.clear()
        }

        Platform.runLater {
            setValueOfChannelOneCheckBox.isSelected = false
            setNulOfChannelOneCheckBox.isSelected = false
            valueOfChannelOneTextField.text = ""
            loadOfChannelOneTextField.text = ""
            valueNameOfChannelOneTextField.isDisable = false
            valueNameOfChannelOneTextField.text = ""
            valueOfChannelOneMultipliersComboBox.selectionModel.select(5)
            loadOfChannelOneMultipliersComboBox.selectionModel.select(5)
            loadOfChannelOneColumn.text = "Величина нагрузки"

            setValueOfChannelTwoCheckBox.isSelected = false
            setNulOfChannelTwoCheckBox.isSelected = false
            valueOfChannelTwoTextField.text = ""
            loadOfChannelTwoTextField.text = ""
            valueNameOfChannelTwoTextField.isDisable = false
            valueNameOfChannelTwoTextField.text = ""
            valueOfChannelTwoMultipliersComboBox.selectionModel.select(5)
            loadOfChannelTwoMultipliersComboBox.selectionModel.select(5)
            loadOfChannelTwoColumn.text = "Величина нагрузки"
        }

        statusBarLine.clear()
        statusBarLine.toggleProgressIndicator(true)
    }

    override fun calibrate(value: Double, submoduleIndex: Int, channelIndex: Int): Double {
        return ltr27CalibrationModel.calibrate(value, submoduleIndex, channelIndex)
    }

    override fun setControllerManager(cm: ControllerManager) {
        this.cm = cm
    }

    override fun setWindowManager(wm: WindowsManager) {
        this.wm = wm
    }
}