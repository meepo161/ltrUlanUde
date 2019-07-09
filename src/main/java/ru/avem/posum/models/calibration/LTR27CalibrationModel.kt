package ru.avem.posum.models.calibration

import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.chart.XYChart

class LTR27CalibrationModel {
    private val calibrationPointsOfChannelOne = mutableListOf<CalibrationPoint>()
    private val calibrationPointsOfChannelTwo = mutableListOf<CalibrationPoint>()
    val bufferedCalibrationPointsOfChannelOne: ObservableList<CalibrationPoint> = FXCollections.observableArrayList()
    val bufferedCalibrationPointsOfChannelTwo: ObservableList<CalibrationPoint> = FXCollections.observableArrayList()
    val lineChartSeriesOfChannelOne = XYChart.Series<Number, Number>()
    val lineChartSeriesOfChannelTwo = XYChart.Series<Number, Number>()

    init {
        lineChartSeriesOfChannelOne.name = "Первый канал"
        lineChartSeriesOfChannelTwo.name = "Второй канал"
    }

    fun addPointToGraphOfChannelOne(calibrationPoint: CalibrationPoint) {
        Platform.runLater {
            val point = XYChart.Data<Number, Number>(calibrationPoint.valueOfChannel, calibrationPoint.loadOfChannel)
            lineChartSeriesOfChannelOne.data.add(point)
        }
    }

    fun addPointToGraphOfChannelTwo(calibrationPoint: CalibrationPoint) {
        Platform.runLater {
            val point = XYChart.Data<Number, Number>(calibrationPoint.valueOfChannel, calibrationPoint.loadOfChannel)
            lineChartSeriesOfChannelTwo.data.add(point)
        }
    }

    fun calibrate(value: Double, isChannelOne: Boolean): Double {
        return if (isChannelOne) {
            getCalibrated(value, calibrationPointsOfChannelOne)
        } else {
            getCalibrated(value, calibrationPointsOfChannelTwo)
        }
    }

    private fun getCalibrated(value: Double, calibrationPoints: List<CalibrationPoint>): Double {
        return if (calibrationPoints.isNotEmpty() && calibrationPoints.size % 2 == 0) {
            if (calibrationPoints.first().valueOfChannel > calibrationPoints.last().valueOfChannel) {
                calibrate(value, calibrationPoints.asReversed())
            } else {
                calibrate(value, calibrationPoints)
            }
        } else {
            value
        }
    }

    private fun calibrate(value: Double, calibrationPoints: List<CalibrationPoint>): Double {
        val lowerBound = calibrationPoints.first().valueOfChannel
        val upperBound = calibrationPoints.last().valueOfChannel
        return when {
            value < lowerBound -> calibrationPoints.first().loadOfChannel
            value > upperBound -> calibrationPoints.last().loadOfChannel
            else -> value * (calibrationPoints.last().loadOfChannel / calibrationPoints.last().valueOfChannel)
        }
    }

    fun clearBuffer() {
        bufferedCalibrationPointsOfChannelOne.clear()
        bufferedCalibrationPointsOfChannelTwo.clear()
    }

    fun updateGraph() {
        lineChartSeriesOfChannelOne.data.clear()
        lineChartSeriesOfChannelTwo.data.clear()

        for (calibrationPoint in calibrationPointsOfChannelOne) {
            addPointToGraphOfChannelOne(calibrationPoint)
        }
        for (calibrationPoint in calibrationPointsOfChannelTwo) {
            addPointToGraphOfChannelTwo(calibrationPoint)
        }
    }

    fun save() {
        calibrationPointsOfChannelOne.clear()
        calibrationPointsOfChannelTwo.clear()
        calibrationPointsOfChannelOne.addAll(bufferedCalibrationPointsOfChannelOne)
        calibrationPointsOfChannelTwo.addAll(bufferedCalibrationPointsOfChannelTwo)
    }

    fun load() {
        bufferedCalibrationPointsOfChannelOne.addAll(calibrationPointsOfChannelOne)
        bufferedCalibrationPointsOfChannelTwo.addAll(calibrationPointsOfChannelTwo)
    }
}
