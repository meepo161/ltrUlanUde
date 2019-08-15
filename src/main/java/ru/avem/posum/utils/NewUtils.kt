package ru.avem.posum.utils

import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt

object NewUtils {
    private const val DECIMAL_SCALE_LIMIT = 7 // максимальное количество знаков после запятой

    // Переводит число из экспоненциальной формы представления числа в десятичную
    fun convertFromExponentialFormat(value: Double, digits: Int): String {
        val rounder = 10.0.pow(digits.toDouble())
        val scale = log10(rounder).toInt()
        val convertedValue = String.format("%.7f", value)
        return convertedValue.substring(0, convertedValue.length - (DECIMAL_SCALE_LIMIT - scale))
    }

    // Округляет чило до нужного знака после запятой
    fun roundValue(value: Double, digits: Int): Double {
        val rounder = 10.0.pow(digits.toDouble())

        return (value * rounder).roundToInt().toDouble() / rounder
    }
}