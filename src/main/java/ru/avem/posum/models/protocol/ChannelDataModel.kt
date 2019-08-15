package ru.avem.posum.models.protocol

import ru.avem.posum.models.process.ChannelModel
import java.text.SimpleDateFormat

/**
 * Модель канала для сериализации и десериализации
 */

class ChannelDataModel(
        var name: String, // Название канала
        var chosenParameterIndex: String, // Индекс регулируемого параметра
        var loadsCounter: String, // Количество нагружений
        var neededAmplitude: String, // Заданное значение амплитуды
        var neededDc: String, // Заданное значение постоянной составляющей
        var neededFrequency: String, // Заданное значение частоты
        var relativeResponseAmplitude: String, // Отношение заданного значения регулируемой величины к измеренному
        var relativeResponseDc: String, // Отношение заданного значения регулируемой величины к измеренному
        var relativeResponseFrequency: String, // Отношение заданного значения регулируемой величины к измеренному
        var responseAmplitude: String, // Измеренное значение амплитуды
        var responseDc: String, // Измеренное значение постоянной составляющей
        var responseFrequency: String, // Измеренное значение частоты
        var rms: String, // Измеренное действующее значение
        var dateAndTime: String // Дата и время проведения измерений
) {
    constructor(channelModel: ChannelModel) : this(
            name = channelModel.name,
            chosenParameterIndex = channelModel.chosenParameterIndex,
            loadsCounter = channelModel.loadsCounter,
            neededAmplitude = channelModel.amplitude,
            neededDc = channelModel.dc,
            neededFrequency = channelModel.frequency,
            relativeResponseAmplitude = channelModel.relativeResponseAmplitude,
            relativeResponseDc = channelModel.relativeResponseDc,
            relativeResponseFrequency = channelModel.relativeResponseFrequency,
            responseAmplitude = channelModel.relativeResponseAmplitude,
            responseDc = channelModel.responseDc,
            responseFrequency = channelModel.responseFrequency,
            rms = channelModel.rms,
            dateAndTime = SimpleDateFormat("dd.MM.yyy HH:mm:ss").format(System.currentTimeMillis())
    )

    // Вовзращает текстовое значение модели
    fun toList(chosenParameters: Array<Boolean>): List<String> {
        val outputList = mutableListOf(
                getDate(),
                getTime(),
                name,
                responseAmplitude,
                responseDc,
                responseFrequency,
                rms,
                loadsCounter
        )

        if (chosenParameterIndex.toInt() != -1) {
            outputList.addAll(getChosenParametersValues(chosenParameters))
        }

        return outputList
    }

    private fun getDate(): String {
        return dateAndTime.split(" ")[0]
    }

    fun getTime(): String {
        return dateAndTime.split(" ")[1]
    }

    private fun getChosenParametersValues(chosenParameters: Array<Boolean>): List<String> {
        val outputList = mutableListOf<String>()

        for ((index, isChosen) in chosenParameters.withIndex()) {
            when (index) {
                0 -> if (isChosen) {
                    outputList.add(neededAmplitude)
                    outputList.add(relativeResponseAmplitude)
                }
                1 -> if (isChosen) {
                    outputList.add(neededDc)
                    outputList.add(relativeResponseDc)
                }
                2 -> if (isChosen) {
                    outputList.add(neededFrequency)
                    outputList.add(relativeResponseFrequency)
                }
            }
        }

        return outputList
    }
}