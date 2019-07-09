package ru.avem.posum.models.calibration

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.chart.XYChart

class LTR27CalibrationModel {
    val calibrationPointsOfChannelOne: ObservableList<CalibrationPoint> = FXCollections.observableArrayList()
    val calibrationPointsOfChannelTwo: ObservableList<CalibrationPoint> = FXCollections.observableArrayList()
    val lineChartSeriesOfChannelOne = XYChart.Series<Number, Number>()
    val lineChartSeriesOfChannelTwo = XYChart.Series<Number, Number>()

    init {
        lineChartSeriesOfChannelOne.name = "Первый канал"
        lineChartSeriesOfChannelTwo.name = "Второй канал"
    }

    fun addPointToGraphOfChannelOne(calibrationPoint: CalibrationPoint) {
        val point = XYChart.Data<Number, Number>(calibrationPoint.valueOfChannel, calibrationPoint.loadOfChannel)
        lineChartSeriesOfChannelOne.data.add(point)
    }

    fun addPointToGraphOfChannelTwo(calibrationPoint: CalibrationPoint) {
        val point = XYChart.Data<Number, Number>(calibrationPoint.valueOfChannel, calibrationPoint.loadOfChannel)
        lineChartSeriesOfChannelTwo.data.add(point)
    }
}