package ru.avem.posum.controllers.calibration

interface LTR27CalibrationManager {
    fun calibrate(value: Double, submoduleIndex: Int, channelIndex: Int): Double

    fun initCalibrationView(title: String, submoduleIndex: Int)

    fun getCalibratedUnits(): List<String>
}