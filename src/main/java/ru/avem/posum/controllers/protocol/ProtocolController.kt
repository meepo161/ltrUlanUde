package ru.avem.posum.controllers.protocol

import javafx.collections.FXCollections
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.stage.FileChooser
import javafx.stage.Stage
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFFont
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import ru.avem.posum.controllers.process.ProcessController
import sun.reflect.generics.tree.VoidDescriptor
import java.io.File
import java.io.FileOutputStream
import kotlin.system.measureTimeMillis

class ProtocolController(val processController: ProcessController) {
    private var workbook = XSSFWorkbook()
    private val maxColons = 100 // maximum number of colons in workbook

    fun createWorkBook(sheets: Array<String>, title: String, titleCellsToMerge: IntArray) {
        createProtocol(*sheets)
        createTitle(title, titleCellsToMerge, *sheets)
    }

    private fun createProtocol(vararg sheetNames: String) {
        workbook = XSSFWorkbook()
        for ((index, name) in sheetNames.withIndex()) {
            workbook.createSheet()
            workbook.setSheetName(index, name)
        }
    }

    private fun createTitle(title: String, cellsToMerge: IntArray, vararg sheets: String) {
        val rowNumber = 0 // row for title
        val columnNumber = 0 // beginning of sheet

        for ((index, sheetName) in sheets.withIndex()) {
            val endCell = if (cellsToMerge[index] > 1) cellsToMerge[index] - 1 else 0
            val sheet = workbook.getSheet(sheetName)
            val row = sheet.createRow(rowNumber)
            row.heightInPoints = 30.0f

            for (index in columnNumber..endCell) {
                val cell = row.createCell(index)
                cell.cellStyle = getTitleStyle()
            }

            val cell = row.getCell(columnNumber)
            cell.setCellValue(title)
            if (cellsToMerge[index] > 1) sheet.addMergedRegion(CellRangeAddress(rowNumber, rowNumber, columnNumber, endCell))
            autosizeColumns(sheetName)
        }
    }

    private fun getTitleStyle(): XSSFCellStyle {
        val cellStyle = workbook.createCellStyle()
        cellStyle.alignment = HorizontalAlignment.CENTER
        cellStyle.verticalAlignment = VerticalAlignment.CENTER

        val font = getFont("Arial", 24, true)
        cellStyle.setFont(font)
        return cellStyle
    }

    private fun getFont(name: String, height: Short, isBold: Boolean): XSSFFont {
        val font = workbook.createFont()
        font.fontName = name
        font.fontHeightInPoints = height
        font.bold = isBold
        return font
    }

    fun autosizeColumns(sheetName: String) {
        val sheet = workbook.getSheet(sheetName)
        for (index in 0..maxColons) {
            sheet.autoSizeColumn(index, true)

        }
    }

    fun fillWorkBook(sheets: Array<String>, headers: Array<Array<String>>, data: List<List<List<String>>>,
                     colors: List<List<Short>>) {
        for (index in headers.indices) {
            createHeaders(sheets[index], *headers[index])
        }
        for (index in sheets.indices) {
            fill(sheets[index], colors[index], data[index])
            autosizeColumns(sheets[index])
        }
    }

    private fun createHeaders(sheetName: String, vararg headers: String) {
        val rowNumber = 1 // row for headers
        for ((index, header) in headers.withIndex()) {
            val sheet = workbook.getSheet(sheetName)
            val row = if (sheet.getRow(rowNumber) == null) sheet.createRow(rowNumber) else sheet.getRow(rowNumber)
            row.heightInPoints = 20.0f
            val cell = row.createCell(index)
            cell.setCellValue(header)
            cell.cellStyle = getHeaderStyle()
        }
    }

    private fun getHeaderStyle(): XSSFCellStyle {
        val cellStyle = workbook.createCellStyle()
        cellStyle.alignment = HorizontalAlignment.CENTER
        cellStyle.verticalAlignment = VerticalAlignment.CENTER
        cellStyle.borderTop = BorderStyle.MEDIUM
        cellStyle.borderRight = BorderStyle.MEDIUM
        cellStyle.borderBottom = BorderStyle.MEDIUM
        cellStyle.borderLeft = BorderStyle.MEDIUM

        val font = getFont("Arial", 16, true)
        cellStyle.setFont(font)
        return cellStyle
    }

    private fun fill(sheetName: String, colors: List<Short>, dataForColumns: List<List<String>>) {
        val sheet = workbook.getSheet(sheetName)
        val constrain = 2 // miss the title and headers rows

        for ((columnIndex, data) in dataForColumns.withIndex()) {
            for (rowIndex in constrain until (data.size + constrain)) {
                val row = if (sheet.getRow(rowIndex) == null) sheet.createRow(rowIndex) else sheet.getRow(rowIndex)
                row.heightInPoints = 16.0f
                val cell = if (row.getCell(columnIndex) == null) row.createCell(columnIndex) else row.getCell(columnIndex)
                val value = data[rowIndex - constrain]
                cell.setCellValue(value)
                cell.cellStyle = getDataCellStyle(colors[rowIndex - constrain])
            }
        }
    }

    private fun getDataCellStyle(color: Short): XSSFCellStyle {
        val cellStyle = workbook.createCellStyle()
        cellStyle.borderTop = BorderStyle.THIN
        cellStyle.borderRight = BorderStyle.THIN
        cellStyle.borderBottom = BorderStyle.THIN
        cellStyle.borderLeft = BorderStyle.THIN
        cellStyle.fillForegroundColor = color
        cellStyle.fillPattern = FillPatternType.SOLID_FOREGROUND

        val font = getFont("Times New Roman", 14, false)
        cellStyle.setFont(font)
        return cellStyle
    }

    fun showFileSaver(windowTitle: String, initialFileName: String): File? {
        val userDocumentsPath = System.getProperty("user.home") + "\\Documents"
        val file = File(userDocumentsPath)
        val fileChooser = FileChooser()
        fileChooser.initialDirectory = file
        fileChooser.initialFileName = initialFileName
        val extensionFilter = FileChooser.ExtensionFilter("XLSX files (*.xlsx)", "*.xlsx")
        fileChooser.extensionFilters.add(extensionFilter)
        fileChooser.title = windowTitle
        return fileChooser.showSaveDialog(Stage())
    }

    fun saveProtocol(selectedDirectory: File, successfulStatus: String) {
        var path = selectedDirectory.absolutePath
        path = if (path.contains(".xlsx")) path else "$path.xlsx"
        val outputStream = FileOutputStream(path)
        workbook.write(outputStream)
        workbook.close()
        processController.statusBarLine.setStatus(successfulStatus + path, true)
    }

    fun showProtocolSaverDialog(): Boolean? {
        val dialog = createDialog()
        val buttons = createButtons()
        val grid = createGrid()

        dialog.dialogPane.content = grid
        dialog.dialogPane.buttonTypes.addAll(buttons.first, buttons.second)
        setButtonsStyle(dialog)
        dialog.dialogPane.content.style = "-fx-font-size: 13px;"
        setResult(dialog)
        val result = dialog.showAndWait()
        return  result.get()
    }

    private fun createDialog(): Dialog<Boolean> {
        val dialog = Dialog<Boolean>()
        dialog.title = "Параметры сохранения протокола"
        dialog.headerText = "Задайте необходимые параметры"
        return dialog
    }

    private fun createButtons(): Pair<ButtonType, ButtonType> {
        val saveButton = ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE)
        val cancelButton = ButtonType("Отмена", ButtonBar.ButtonData.OK_DONE)
        return Pair(saveButton, cancelButton)
    }

    private fun setButtonsStyle(dialog: Dialog<Boolean>) {
        val buttonBar = dialog.dialogPane.lookup(".button-bar") as ButtonBar
        buttonBar.stylesheets.add(javaClass.getResource("buttons.css").toExternalForm())
    }

    private fun createGrid(): GridPane {
        val grid = GridPane()
        grid.hgap = 10.0
        grid.vgap = 10.0
        grid.add(Label("Проредить по:"), 0, 0)
        grid.add(createComboBox(), 1, 0)
        return grid
    }

    private fun createComboBox(): ComboBox<String> {
        val comboBox = ComboBox<String>()
        val items = FXCollections.observableArrayList<String>("Без прореживания", "5 секунд", "10 секунд",
                "30 секунд", "1 минуте", "10 минут", "30 минут", "1 часу", "12 часов")
        comboBox.items = items
        comboBox.selectionModel.select("Без прореживания")
        comboBox.stylesheets.add(javaClass.getResource("combo-box.css").toExternalForm())
        return comboBox
    }

    private fun setResult(dialog: Dialog<Boolean>) {
        val saveButton = dialog.dialogPane.buttonTypes[0]
        dialog.setResultConverter { it == saveButton }
    }
}
