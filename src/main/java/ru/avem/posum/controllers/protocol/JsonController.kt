package ru.avem.posum.controllers.protocol

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okio.Okio
import ru.avem.posum.models.process.ChannelModel
import ru.avem.posum.models.protocol.ChannelDataModel
import java.io.File
import java.io.FileNotFoundException
import java.text.SimpleDateFormat

class JsonController(private val path: String) {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val channelsDataAdapter = moshi.adapter(ChannelDataModel::class.java)

    init {
        createFile()
    }

    fun createFile() {
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

    private fun load(path: String): List<ChannelDataModel>? {
        val listType = Types.newParameterizedType(List::class.java, ChannelDataModel::class.java)
        val jsonAdapter: JsonAdapter<List<ChannelDataModel>> = moshi.adapter(listType)
        val json = File(path).readText()
        return jsonAdapter.fromJson(json)
    }

    fun close(path: String) {
        val json = File(path).readText().removeSuffix(",\n")
        if (json.last() != ']') File(path).writeText("$json\n]") // end of list
    }

    fun shortClose() {
        val buffer = File(path).readText()
        val tempPath = "$path.temp"
        File(tempPath).createNewFile()
        File(tempPath).writeText(buffer)
        close(tempPath)
    }

    fun parse(file: File) {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val bufferedSource = Okio.buffer(Okio.source(file))
        val adapter: JsonAdapter<ChannelDataModel> = moshi.adapter(ChannelDataModel::class.java)

        while (!bufferedSource.exhausted()) {
            val line: String = bufferedSource.readUtf8Line() ?: break
            val json = if (line.first() != '{') line.substring(1) else line
            val channelDataModel = adapter.fromJson(json)

        }
    }

    fun parse(isPointData: Boolean, isShort: Boolean, rarefactionCoefficient: Long): List<ChannelModel> {
        val channelsModels = mutableListOf<ChannelModel>()
        val channelsDataModels = if (isShort || isPointData) load("$path.temp") else load(path)
        val timeFormat = SimpleDateFormat("hh:mm:ss")
        if (channelsDataModels != null) {
            var time = timeFormat.parse(channelsDataModels[0].dateAndTime.split(" ")[1]).time
            var timeIsChanged = false
            for (channelData in channelsDataModels) {
                val channelModel = ChannelModel(channelData.name)
                val channelTime = timeFormat.parse(channelData.dateAndTime.split(" ")[1]).time
                if (channelTime == time || rarefactionCoefficient == 1000.toLong()) {
                    channelModel.amplitude = channelData.neededAmplitude
                    channelModel.dc = channelData.neededDc
                    channelModel.frequency = channelData.neededFrequency
                    channelModel.rms = channelData.rms
                    channelModel.loadsCounter = channelData.loadsCounter
                    channelModel.chosenParameterIndex = channelData.chosenParameterIndex
                    channelModel.responseAmplitude = channelData.responseAmplitude
                    channelModel.responseDc = channelData.responseDc
                    channelModel.responseFrequency = channelData.responseFrequency
                    channelModel.relativeResponseAmplitude = channelData.relativeResponseAmplitude
                    channelModel.relativeResponseDc = channelData.relativeResponseDc
                    channelModel.relativeResponseFrequency = channelData.relativeResponseFrequency
                    channelModel.date = channelData.dateAndTime.split(" ")[0]
                    channelModel.time = channelData.dateAndTime.split(" ")[1]
                    channelsModels.add(channelModel)
                    timeIsChanged = false
                } else if (!timeIsChanged) {
                    time += rarefactionCoefficient
                    timeIsChanged = true
                }
            }

            if (isShort) return getShortList(channelsModels)
        }
        return channelsModels
    }

    private fun getShortList(channelsModels: List<ChannelModel>): List<ChannelModel> {
        val timeFormat = SimpleDateFormat("hh:mm:ss")
        val time = timeFormat.parse(channelsModels.last().time).time
        val timeLimit = 60_000 // maximum dateAndTime interval in mills
        val shortList = mutableListOf<ChannelModel>()

        for (channelModel in channelsModels.asReversed()) {
            val channelTime = timeFormat.parse(channelModel.time).time
            if (time - channelTime < timeLimit) shortList.add(channelModel)
        }

        return shortList.asReversed()
    }
}