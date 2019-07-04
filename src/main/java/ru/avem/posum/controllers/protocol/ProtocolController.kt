package ru.avem.posum.controllers.protocol

import javafx.collections.FXCollections
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.stage.FileChooser
import javafx.stage.Stage
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.usermodel.charts.AxisPosition
import org.apache.poi.ss.usermodel.charts.ChartDataSource
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xddf.usermodel.chart.LegendPosition
import ru.avem.posum.controllers.process.ProcessController
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import org.apache.poi.ss.usermodel.charts.DataSources
import org.apache.poi.xssf.usermodel.*
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean

class ProtocolController(val processController: ProcessController) {
    private val amplitudeColumnIndex = 3
    private val dcColumnIndex = 4
    private val firstRowIndex = 2 // miss the title row and headers row
    private val frequencyColumnIndex = 5
    private val maxColons = 100 // maximum number of colons in workbook
    private val rowShifter = 10 // height of lineChart in rows
    private val rmsColumnIndex = 6
    private val timeColumnIndex = 1
    private var workbook = XSSFWorkbook()
    private val cellStyles = HashMap<CellStyles, XSSFCellStyle>()


    fun createProtocol(testProgramId: Long, testProgramTitle: String, isPointData: Boolean, isShort: Boolean,
                       rarefactionCoefficient: Long, vararg sheetsNames: ProtocolSheets) {

        val headers = mutableListOf<Array<String>>()
        val data = mutableListOf<List<List<String>>>()
        val colors = mutableListOf<List<Short>>()
        val cellsToMerge = mutableListOf<Int>()

        for (sheetName in sheetsNames) {
            when (sheetName) {
                ProtocolSheets.GENERAL_DESCRIPTION -> {
                    headers.add(processController.testProgramController.getTestProgramHeaders())
                    data.add(processController.testProgramController.getTestProgramData())
                    colors.add(processController.testProgramController.getColorsForProtocol())
                    cellsToMerge.add(processController.testProgramController.getCellsToMerge())
                }
                ProtocolSheets.CHANNELS_DATA -> {
                    headers.add(processController.tableController.columnsHeaders)
                    data.add(listOf())
                    colors.add(listOf())
                    cellsToMerge.add(processController.tableController.cellsToMerge)
                }
                ProtocolSheets.JOURNAL -> {
                    headers.add(processController.eventsController.journalHeaders)
                    data.add(processController.eventsController.getEvents(testProgramId))
                    colors.add(processController.eventsController.getEventsColors(testProgramId))
                    cellsToMerge.add(processController.eventsController.cellsToMerge)
                }
                ProtocolSheets.COMMANDS -> {
                    headers.add(processController.commandsController.commandsHeaders)
                    data.add(processController.commandsController.getCommands(testProgramId))
                    colors.add(processController.commandsController.getCommandsColors(testProgramId))
                    cellsToMerge.add(processController.commandsController.cellsToMerge)
                }
            }
        }

        // Create the workbook
        val sheets = sheetsNames.map { it.sheetName }.toTypedArray()
        createWorkBook(sheets, testProgramTitle, cellsToMerge.toIntArray())
        fillWorkBook(sheets, headers.toTypedArray(), data, colors, isPointData, isShort, rarefactionCoefficient)
    }

    private fun createWorkBook(sheets: Array<String>, title: String, titleCellsToMerge: IntArray) {
        createProtocol(*sheets)
        createTitle(title, titleCellsToMerge, *sheets)
        createDataCellsStyles()
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

            for (columnIndex in columnNumber..endCell) {
                val cell = row.createCell(columnIndex)
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

    private fun autosizeColumns(sheetName: String) {
        val sheet = workbook.getSheet(sheetName)
        for (index in 0..maxColons) {
            sheet.autoSizeColumn(index, true)

        }
    }

    private fun fillWorkBook(sheets: Array<String>, headers: Array<Array<String>>, data: List<List<List<String>>>,
                             colors: List<List<Short>>, isPointData: Boolean, isShort: Boolean, rarefactionCoefficient: Long) {
        for (index in headers.indices) {
            createHeaders(sheets[index], *headers[index])
        }
        for (index in sheets.indices) {
            fill(sheets[index], colors[index], data[index])
            autosizeColumns(sheets[index])
        }

        if (sheets.any { it == ProtocolSheets.CHANNELS_DATA.sheetName }) {
            when {
                isPointData -> processController.jsonController.parseOneSecond()
                isShort -> {
                    processController.jsonController.parsePieceOfFile()
                    drawLineChart(ProtocolSheets.CHANNELS_DATA.sheetName)
                }
                else -> {
                    processController.jsonController.parseFullFile(rarefactionCoefficient)
                    drawLineChart(ProtocolSheets.CHANNELS_DATA.sheetName)
                }
            }

            autosizeColumns(ProtocolSheets.CHANNELS_DATA.sheetName)
        }
    }

    private fun createHeaders(sheetName: String, vararg headers: String) {
        val sheet = workbook.getSheet(sheetName)
        val rowNumber = 1 // row for headers
        for ((index, header) in headers.withIndex()) {
            val row = if (sheet.getRow(rowNumber) == null) sheet.createRow(rowNumber) else sheet.getRow(rowNumber)
            row.heightInPoints = 20.0f
            val cell = row.createCell(index)
            cell.setCellValue(header)
            cell.cellStyle = getHeaderStyle()
        }
        sheet.createFreezePane(0, 2)
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

        for ((columnIndex, data) in dataForColumns.withIndex()) {
            for (rowIndex in firstRowIndex until (data.size + firstRowIndex)) {
                val row = if (sheet.getRow(rowIndex) == null) sheet.createRow(rowIndex) else sheet.getRow(rowIndex)
                row.heightInPoints = 16.0f
                val cell = if (row.getCell(columnIndex) == null) row.createCell(columnIndex) else row.getCell(columnIndex)
                val value = data[rowIndex - firstRowIndex]
                cell.setCellValue(value)
                cell.cellStyle = createDataCellStyle(colors[rowIndex - firstRowIndex])
            }
        }
    }

    fun fillChannelData(channelData: List<String>) {
        val sheet = workbook.getSheet(ProtocolSheets.CHANNELS_DATA.sheetName)
        val row = sheet.createRow(sheet.lastRowNum + 1)
        row.heightInPoints = 16.0f

        for ((columnIndex, data) in channelData.withIndex()) {
            val cell = row.createCell(columnIndex)
            cell.setCellValue(data)
            cell.cellStyle = cellStyles[CellStyles.LOG]
        }
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
        try {
            val outputStream = FileOutputStream(path)
            workbook.write(outputStream)
            workbook.close()
            processController.statusBarLine.setStatus(successfulStatus + path, true)
        } catch (e: FileNotFoundException) {
            processController.statusBarLine.setStatus("Ошибка сохранения - файл занят другим процессом", false)
        }
    }

    fun showProtocolSaverDialog(): Long {
        val dialog = createDialog()
        val combobox = createComboBox()
        val buttons = createButtons()
        val grid = createGrid(combobox)
        dialog.dialogPane.content = grid
        dialog.dialogPane.buttonTypes.addAll(buttons.first, buttons.second)
        setButtonsStyle(dialog)
        dialog.dialogPane.content.style = "-fx-font-size: 13px;"
        setResult(dialog, combobox)
        return dialog.showAndWait().get()
    }

    private fun createDialog(): Dialog<Long> {
        val dialog = Dialog<Long>()
        dialog.title = "Параметры сохранения протокола"
        dialog.headerText = "Задайте необходимые параметры"
        return dialog
    }

    private fun createButtons(): Pair<ButtonType, ButtonType> {
        val saveButton = ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE)
        val cancelButton = ButtonType("Отмена", ButtonBar.ButtonData.OK_DONE)
        return Pair(saveButton, cancelButton)
    }

    private fun setButtonsStyle(dialog: Dialog<Long>) {
        val buttonBar = dialog.dialogPane.lookup(".button-bar") as ButtonBar
        buttonBar.stylesheets.add(javaClass.getResource("buttons.css").toExternalForm())
    }

    private fun createGrid(combobox: ComboBox<String>): GridPane {
        val grid = GridPane()
        grid.hgap = 10.0
        grid.vgap = 10.0
        grid.add(Label("Проредить по:"), 0, 0)
        grid.add(combobox, 1, 0)
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

    private fun setResult(dialog: Dialog<Long>, combobox: ComboBox<String>) {
        val saveButton = dialog.dialogPane.buttonTypes[0]
        dialog.setResultConverter {
            if (it == saveButton) parseRarefactionCoefficient(combobox.selectionModel.selectedItem)
            else -1
        }
    }

    private fun parseRarefactionCoefficient(string: String): Long {
        var coefficient: Long = 1 // default value
        when (string) {
            "Без прореживания" -> coefficient = 1000
            "5 секунд" -> coefficient = 5000
            "10 секунд" -> coefficient = 10_000
            "30 секунд" -> coefficient = 30_000
            "1 минуте" -> coefficient = 60_000
            "10 минут" -> coefficient = 600_000
            "30 минут" -> coefficient = 1_800_000
            "1 часу" -> coefficient = 3_600_000
            "12 часов" -> coefficient = 43_200_000
        }
        return coefficient
    }

    private fun drawLineChart(sheetName: String) {
        val sheet = workbook.getSheet(sheetName)
        val lastRowIndex = getLastRowIndex(sheet) + 1
        val timeData = DataSources.fromNumericCellRange(sheet, CellRangeAddress(firstRowIndex, lastRowIndex, timeColumnIndex, timeColumnIndex))
        val amplitudeData = DataSources.fromNumericCellRange(sheet, CellRangeAddress(firstRowIndex, lastRowIndex, amplitudeColumnIndex, amplitudeColumnIndex))
        val dcData = DataSources.fromNumericCellRange(sheet, CellRangeAddress(firstRowIndex, lastRowIndex, dcColumnIndex, dcColumnIndex))
        val frequencyData = DataSources.fromNumericCellRange(sheet, CellRangeAddress(firstRowIndex, lastRowIndex, frequencyColumnIndex, frequencyColumnIndex))
        val rmsData = DataSources.fromNumericCellRange(sheet, CellRangeAddress(firstRowIndex, lastRowIndex, rmsColumnIndex, rmsColumnIndex))

        var lineChart = createLineChart(sheet, "График амплитуды", 0)
        drawLineChart(lineChart, "Амплитуда", timeData, amplitudeData)

        lineChart = createLineChart(sheet, "График статики", (rowShifter + 1))
        drawLineChart(lineChart, "Статика", timeData, dcData)

        lineChart = createLineChart(sheet, "График частоты", 2 * (rowShifter + 1))
        drawLineChart(lineChart, "Частота", timeData, frequencyData)

        lineChart = createLineChart(sheet, "График rms", 3 * (rowShifter + 1))
        drawLineChart(lineChart, "Rms", timeData, rmsData)
    }

    private fun createLineChart(sheet: XSSFSheet, lineChartTitle: String, chartBeginningRow: Int): XSSFChart {
        val drawing = sheet.createDrawingPatriarch()
        val chartBeginningColumnIndex = getLastColumnIndex(sheet) + 2
        val anchor = drawing.createAnchor(0, 0, 0, 0, chartBeginningColumnIndex,
                firstRowIndex + chartBeginningRow, 20,
                firstRowIndex + chartBeginningRow + rowShifter)
        val lineChart = drawing.createChart(anchor)
        lineChart.setTitleText(lineChartTitle)

        return lineChart
    }

    private fun drawLineChart(lineChart: XSSFChart, seriesTitle: String, xAxisData: ChartDataSource<Number>, yAxisData: ChartDataSource<Number>) {
        val legend = lineChart.orAddLegend
        legend.position = LegendPosition.RIGHT

        val data = lineChart.chartDataFactory.createLineChartData()

        val xAxis = lineChart.chartAxisFactory.createCategoryAxis(AxisPosition.BOTTOM)
        val yAxis = lineChart.createValueAxis(AxisPosition.LEFT)
        yAxis.crosses = org.apache.poi.ss.usermodel.charts.AxisCrosses.AUTO_ZERO

        val series = data.addSeries(xAxisData, yAxisData)
        series.setTitle(seriesTitle)
        lineChart.plot(data, xAxis, yAxis)

        val plotArea = lineChart.ctChart.plotArea
        plotArea.lineChartArray[0].smooth
        val ctBool = CTBoolean.Factory.newInstance()
        ctBool.`val` = false
        plotArea.lineChartArray[0].smooth = ctBool
        for (series in plotArea.lineChartArray[0].serArray) {
            series.smooth = ctBool
        }
    }

    private fun getLastColumnIndex(sheet: XSSFSheet): Int {
        var lastColumnNum: Short = 0
        for (index in 0..sheet.lastRowNum) {
            val lastCellNum = sheet.getRow(index).lastCellNum
            if (sheet.getRow(index).lastCellNum > lastColumnNum) lastColumnNum = lastCellNum
        }
        return lastColumnNum.toInt() - 1
    }

    private fun getLastRowIndex(sheet: XSSFSheet): Int {
        return sheet.lastRowNum - 1
    }

    private fun createDataCellsStyles() {
        cellStyles[CellStyles.ERROR] = createDataCellStyle(IndexedColors.RED.index)
        cellStyles[CellStyles.LOG] = createDataCellStyle(IndexedColors.WHITE.index)
        cellStyles[CellStyles.OK] = createDataCellStyle(IndexedColors.GREEN.index)
        cellStyles[CellStyles.WARNING] = createDataCellStyle(IndexedColors.YELLOW.index)
    }


    private fun createDataCellStyle(color: Short): XSSFCellStyle {
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
}
