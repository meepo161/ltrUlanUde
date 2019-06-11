package ru.avem.posum.controllers.protocol

import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFFont
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import ru.avem.posum.controllers.process.ProcessController
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Paths

open class ProtocolController(val processController: ProcessController) {
    private var workbook = XSSFWorkbook()
    private val maxColons = 100 // maximum number of colons in workbook

    open fun createProtocol(vararg sheetNames: String) {
        workbook = XSSFWorkbook()
        for ((index, name) in sheetNames.withIndex()) {
            workbook.createSheet()
            workbook.setSheetName(index, name)
        }
    }

    open fun createTitle(title: String, cellsToMerge: IntArray, vararg sheets: String) {
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

    open fun autosizeColumns(sheetName: String) {
        val sheet = workbook.getSheet(sheetName)
        for (index in 0..maxColons) {
            sheet.autoSizeColumn(index, true)

        }
    }

    open fun createHeaders(sheetName: String, vararg headers: String) {
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

    open fun fill(sheetName: String, colors: List<Short>, dataForColumns: List<List<String>>) {
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

    open fun saveProtocol(path: String) {
        val outputStream = FileOutputStream(path)
        workbook.write(outputStream)
        workbook.close()
    }

    open fun addSheet(file: File, sheetName: String) {
        workbook = WorkbookFactory.create(file) as XSSFWorkbook
        workbook.createSheet(sheetName).isSelected = true
        workbook.setSheetOrder(sheetName, 0)
    }

    open fun overrideFile(path: String) {
        val bufferedPath = "$path.new"
        val outputString = FileOutputStream(bufferedPath)
        workbook.write(outputString)
        Files.delete(Paths.get(path))
        Files.move(Paths.get(bufferedPath), Paths.get(path))
    }
}