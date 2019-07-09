package ru.avem.posum.controllers.calibration

interface LTR27CalibrationManager {
    fun calibrate(value: Double, isChannelOne: Boolean): Double
}