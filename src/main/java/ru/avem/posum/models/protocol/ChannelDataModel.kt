package ru.avem.posum.models.protocol

import java.io.Serializable

class ChannelDataModel(val name: String) : Serializable {
    var chosenParameterIndex = -1
    var loadsCounter = 0
    var neededAmplitude = 0.0
    var neededDc = 0.0
    var neededFrequency = 0.0
    var relativeResponseAmplitude = 0.0
    var relativeResponseDc = 0.0
    var relativeResponseFrequency = 0.0
    var responseAmplitude = 0.0
    var responseDc = 0.0
    var responseFrequency = 0.0
    var rms = 0.0
    var time = "00.00.00 00:00:00"
}