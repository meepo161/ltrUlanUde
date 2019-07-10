package ru.avem.posum.controllers.calibration

interface LTR27CalibrationManager {
    fun calibrate(value: Double): Double

    fun initCalibrationView(title: String, submoduleIndex: Int)
}