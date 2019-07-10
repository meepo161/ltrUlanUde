package ru.avem.posum.controllers.calibration

interface LTR27CalibrationManager {
    fun calibrate(isCalibrate: Boolean, value: Double, submoduleIndex: Int, channelIndex: Int): Double

    fun initCalibrationView(title: String, submoduleIndex: Int)

    fun getCalibratedUnits(): List<String>
}