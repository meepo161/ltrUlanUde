package ru.avem.posum.controllers.protocol

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import ru.avem.posum.models.process.ChannelModel
import ru.avem.posum.models.protocol.ChannelDataModel
import java.io.File
import java.io.FileNotFoundException

class JsonController(private val path: String) {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val channelsDataAdapter = moshi.adapter(ChannelDataModel::class.java)

    init {
        createFile()
    }

    private fun createFile() {
        try {
            File(path).writeText("[\n") // beginning of list
        } catch (e: FileNotFoundException) {
            File(path).createNewFile()
        }
    }

    fun save(channelsData: List<ChannelDataModel>) {
        for (channelData in channelsData) {
            val json = channelsDataAdapter.toJson(channelData)
            File(path).appendText("$json,\n")
        }
    }

    private fun load(): List<ChannelDataModel>? {
        val listType = Types.newParameterizedType(List::class.java, ChannelDataModel::class.java)
        val jsonAdapter: JsonAdapter<List<ChannelDataModel>> = moshi.adapter(listType)
        val json = File(path).readText()
        return jsonAdapter.fromJson(json)
    }

    fun close() {
        val json = File(path).readText().removeSuffix(",\n")
        File(path).writeText("$json\n]")
    } // end of list

    fun parse(): List<ChannelModel> {
        val channelsModels = mutableListOf<ChannelModel>()
        val channelsDataModels = load()
        if (channelsDataModels != null) {
            for (channelData in channelsDataModels) {
                val channelModel = ChannelModel(channelData.name)
                channelModel.amplitude = channelData.neededAmplitude.toString()
                channelModel.dc = channelData.neededDc.toString()
                channelModel.frequency = channelData.neededFrequency.toString()
                channelModel.rms = channelData.rms.toString()
                channelModel.loadsCounter = channelData.loadsCounter.toString()
                channelModel.chosenParameterIndex = channelData.chosenParameterIndex.toString()
                channelModel.responseAmplitude = channelData.responseAmplitude.toString()
                channelModel.responseDc = channelData.responseDc.toString()
                channelModel.responseFrequency = channelData.responseFrequency.toString()
                channelModel.relativeResponseAmplitude = channelData.relativeResponseAmplitude.toString()
                channelModel.relativeResponseDc = channelData.relativeResponseDc.toString()
                channelModel.relativeResponseFrequency = channelData.relativeResponseFrequency.toString()
                channelModel.date = channelData.time.split(" ")[0]
                channelModel.time = channelData.time.split(" ")[1]
                channelsModels.add(channelModel)
            }
        }
        return channelsModels
    }
}