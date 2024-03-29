package ru.avem.posum.models.calibration

import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.chart.XYChart
import ru.avem.posum.hardware.LTR27

class LTR27CalibrationModel {
    private val calibrationPointsOfChannelOne = Array<MutableList<CalibrationPoint>>(LTR27.MAX_SUBMODULES) { mutableListOf() }
    private val calibrationPointsOfChannelTwo = Array<MutableList<CalibrationPoint>>(LTR27.MAX_SUBMODULES) { mutableListOf() }
    val bufferedCalibrationPointsOfChannelOne = Array<ObservableList<CalibrationPoint>>(LTR27.MAX_SUBMODULES) { FXCollections.observableArrayList() }
    val bufferedCalibrationPointsOfChannelTwo = Array<ObservableList<CalibrationPoint>>(LTR27.MAX_SUBMODULES) { FXCollections.observableArrayList() }
    val lineChartSeriesOfChannelOne = XYChart.Series<Number, Number>()
    val lineChartSeriesOfChannelTwo = XYChart.Series<Number, Number>()

    init {
        for (index in 0 until LTR27.MAX_SUBMODULES) {
            lineChartSeriesOfChannelOne.name = "Первый канал"
            lineChartSeriesOfChannelTwo.name = "Второй канал"
        }
    }

    // Добавляет градуировочную точку первого канала
    fun addPointToGraphOfChannelOne(calibrationPoint: CalibrationPoint) {
        Platform.runLater {
            val point = XYChart.Data<Number, Number>(calibrationPoint.valueOfChannel, calibrationPoint.loadOfChannel)
            lineChartSeriesOfChannelOne.data.add(point)
        }
    }

    // Добавляет градуировочную точку второго канала
    fun addPointToGraphOfChannelTwo(calibrationPoint: CalibrationPoint) {
        Platform.runLater {
            val point = XYChart.Data<Number, Number>(calibrationPoint.valueOfChannel, calibrationPoint.loadOfChannel)
            lineChartSeriesOfChannelTwo.data.add(point)
        }
    }

    // Градуирует значение
    fun calibrate(isCalibrate: Boolean, value: Double, submoduleIndex: Int, channelIndex: Int): Double {
        return if (channelIndex % 2 == 0) {
            getCalibrated(isCalibrate, value, calibrationPointsOfChannelOne[submoduleIndex])
        } else {
            getCalibrated(isCalibrate, value, calibrationPointsOfChannelTwo[submoduleIndex])
        }
    }

    // Возвращает градуированное значение
    private fun getCalibrated(isCalibrate: Boolean, value: Double, calibrationPoints: List<CalibrationPoint>): Double {
        return if (isCalibrate && calibrationPoints.isNotEmpty() && calibrationPoints.size % 2 == 0) {
            if (calibrationPoints.first().valueOfChannel > calibrationPoints.last().valueOfChannel) {
                calibrate(value, calibrationPoints.asReversed())
            } else {
                calibrate(value, calibrationPoints)
            }
        } else {
            value
        }
    }

    // Градуирует значение
    private fun calibrate(value: Double, calibrationPoints: List<CalibrationPoint>): Double {

        return (-(calibrationPoints.first().valueOfChannel * calibrationPoints.last().loadOfChannel -
                calibrationPoints.last().valueOfChannel * calibrationPoints.first().loadOfChannel) -
                (calibrationPoints.first().loadOfChannel - calibrationPoints.last().loadOfChannel) * value) /
                (calibrationPoints.last().valueOfChannel - calibrationPoints.first().valueOfChannel)
//        return value * (calibrationPoints.last().loadOfChannel / calibrationPoints.last().valueOfChannel)
    }

    // Очищает буфер
    fun clearBuffer(submoduleIndex: Int) {
        for (index in 0 until LTR27.MAX_SUBMODULES) {
            bufferedCalibrationPointsOfChannelOne[submoduleIndex].clear()
            bufferedCalibrationPointsOfChannelTwo[submoduleIndex].clear()
        }
    }

    // Обновляет градуировочный график субмодуля
    fun updateGraph(submoduleIndex: Int) {
        for (calibrationPoint in calibrationPointsOfChannelOne[submoduleIndex]) {
            addPointToGraphOfChannelOne(calibrationPoint)
        }
        for (calibrationPoint in calibrationPointsOfChannelTwo[submoduleIndex]) {
            addPointToGraphOfChannelTwo(calibrationPoint)
        }
    }

    // Сохраняет градуировочные точки
    fun save(submoduleIndex: Int) {
        calibrationPointsOfChannelOne[submoduleIndex].clear()
        calibrationPointsOfChannelTwo[submoduleIndex].clear()
        calibrationPointsOfChannelOne[submoduleIndex].addAll(bufferedCalibrationPointsOfChannelOne[submoduleIndex])
        calibrationPointsOfChannelTwo[submoduleIndex].addAll(bufferedCalibrationPointsOfChannelTwo[submoduleIndex])
    }

    // Загружает градуировочные точки
    fun load(submoduleIndex: Int) {
        bufferedCalibrationPointsOfChannelOne[submoduleIndex].addAll(calibrationPointsOfChannelOne[submoduleIndex])
        bufferedCalibrationPointsOfChannelTwo[submoduleIndex].addAll(calibrationPointsOfChannelTwo[submoduleIndex])
    }

    // Возвращает названия физических величин
    fun getCalibratedUnits(): List<String> {
        val calibratedUnits = mutableListOf<String>()

        for (submoduleIndex in 0 until LTR27.MAX_SUBMODULES) {
            calibratedUnits.add(getUnit(calibrationPointsOfChannelOne[submoduleIndex]))
            calibratedUnits.add(getUnit(calibrationPointsOfChannelTwo[submoduleIndex]))
        }

        return calibratedUnits
    }

    // Возвращает название физической величины
    private fun getUnit(calibrationPoints: List<CalibrationPoint>): String {
        return if (calibrationPoints.isNotEmpty()) {
            calibrationPoints.first().valueName
        } else {
            ""
        }
    }

    // Возвращает список градуировочных точек
    fun getCalibrationPoints(): ArrayList<List<String>> {
        val calibrationPoints = ArrayList<List<String>>()
        for (index in 0 until LTR27.MAX_SUBMODULES) {
            calibrationPoints.add(toListOfStrings(calibrationPointsOfChannelOne[index]))
            calibrationPoints.add(toListOfStrings(calibrationPointsOfChannelTwo[index]))
        }
        return calibrationPoints
    }

    private fun toListOfStrings(calibrationPoints: MutableList<CalibrationPoint>): List<String> {
        val outputList = mutableListOf<String>()
        for (calibrationPoint in calibrationPoints) {
            outputList.add(calibrationPoint.toString())
        }
        return outputList
    }

    // Очищает списки градуировочных точек
    fun clear() {
        for (index in 0 until LTR27.MAX_SUBMODULES) {
            calibrationPointsOfChannelOne[index].clear()
            calibrationPointsOfChannelTwo[index].clear()
        }
    }

    // Задает списки градуировочных точек
    fun setCalibrationPoints(rawData: ArrayList<List<String>>) {
        for (index in 0 until rawData.size) {
            if (index % 2 == 0) {
                for (calibrationPointIndex in 0 until rawData[index].size) {
                    calibrationPointsOfChannelOne[index / 2].add(CalibrationPoint.fromString(rawData[index][calibrationPointIndex]))
                }
            } else {
                for (calibrationPointIndex in 0 until rawData[index].size) {
                    calibrationPointsOfChannelTwo[index / 2].add(CalibrationPoint.fromString(rawData[index][calibrationPointIndex]))
                }
            }
        }
    }
}
