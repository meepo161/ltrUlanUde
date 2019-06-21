package ru.avem.posum.models.protocol

import ru.avem.posum.models.process.ChannelModel
import java.text.SimpleDateFormat

class ChannelDataModel(
        var name: String,
        var chosenParameterIndex: String,
        var loadsCounter: String,
        var neededAmplitude: String,
        var neededDc: String,
        var neededFrequency: String,
        var relativeResponseAmplitude: String,
        var relativeResponseDc: String,
        var relativeResponseFrequency: String,
        var responseAmplitude: String,
        var responseDc: String,
        var responseFrequency: String,
        var rms: String,
        var dateAndTime: String
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

    fun toList(): List<String> {
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
            outputList.addAll(getChosenParameterValue())
        }

        return outputList
    }

    private fun getDate(): String {
        return dateAndTime.split(" ")[0]
    }

    private fun getTime(): String {
        return dateAndTime.split(" ")[1]
    }

    private fun getChosenParameterValue(): List<String> {
        val outputList = mutableListOf<String>()

        when (chosenParameterIndex.toInt()) {
            0 -> {
                outputList.add(neededAmplitude)
                outputList.add(relativeResponseAmplitude)
            }
            1 -> {
                outputList.add(neededDc)
                outputList.add(relativeResponseDc)
            }
            2 -> {
                outputList.add(neededFrequency)
                outputList.add(relativeResponseFrequency)
            }
        }

        return outputList
    }
}