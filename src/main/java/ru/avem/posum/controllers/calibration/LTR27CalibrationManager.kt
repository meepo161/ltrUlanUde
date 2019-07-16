package ru.avem.posum.controllers.calibration

import ru.avem.posum.hardware.LTR27

interface LTR27CalibrationManager {
    fun calibrate(isCalibrate: Boolean, value: Double, submoduleIndex: Int, channelIndex: Int): Double

    fun initCalibrationView(title: String, submoduleIndex: Int)

    fun getCalibratedUnits(): List<String>

    fun saveCalibrationSettings(moduleInstance: LTR27)

    fun loadCalibrationSettings(moduleInstance: LTR27)
}