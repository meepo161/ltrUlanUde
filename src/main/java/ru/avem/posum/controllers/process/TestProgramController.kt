package ru.avem.posum.controllers.process

import org.apache.poi.ss.usermodel.IndexedColors
import ru.avem.posum.db.models.TestProgram
import ru.avem.posum.models.protocol.TestProgramDataModel

class TestProgramController {
    private val testProgramDataModel = TestProgramDataModel()

    // Считывает общие данные программы испытаний
    fun parse(testProgram: TestProgram) {
        testProgramDataModel.name = testProgram.name
        testProgramDataModel.testingSampleName = testProgram.sampleName
        testProgramDataModel.sampleSerialNumber = testProgram.sampleSerialNumber
        testProgramDataModel.documentNumber = testProgram.documentNumber
        testProgramDataModel.typeOfTest = testProgram.testProgramType
        testProgramDataModel.timeOfTest = testProgram.testProgramTime
        testProgramDataModel.dateOfTest = testProgram.testProgramDate
        testProgramDataModel.leadEngineer = testProgram.leadEngineer
        testProgramDataModel.comments = testProgram.comments
    }

    // Возвращает заголовки общих данных о программе испытаний при формировании протокола испытаний
    fun getTestProgramHeaders(): Array<String> {
        return arrayOf("Поле", "Значение")
    }

    // Возвращает общие данные о программе испытаний
    fun getTestProgramData(): List<List<String>> {
        val description = listOf("Название испытания:", "Испытываемый объект:", "Серийный номер объекта:",
                "Номер документа:", "Тип испытаний:", "Длительность испытаний:", "Дата испытаний:", "Ведущий инженер:",
                "Комментарии:")
        val data = listOf(testProgramDataModel.name, testProgramDataModel.testingSampleName,
                testProgramDataModel.sampleSerialNumber, testProgramDataModel.documentNumber,
                testProgramDataModel.typeOfTest, testProgramDataModel.timeOfTest, testProgramDataModel.dateOfTest,
                testProgramDataModel.leadEngineer, testProgramDataModel.comments)

        return listOf(description, data)
    }

    // Возвращает цвета, которыми будет выделена общая информация об испытании при формировании протокола
    fun getColorsForProtocol(): List<Short> {
        val colors = mutableListOf<Short>()
        for (index in 0 until getTestProgramData()[0].size) {
            colors.add(IndexedColors.WHITE.index)
        }
        return colors
    }

    // Возвращает количество ячеек для объединения при формировании протокола испытаний
    fun getCellsToMerge(): Int {
        return getTestProgramHeaders().size
    }
}