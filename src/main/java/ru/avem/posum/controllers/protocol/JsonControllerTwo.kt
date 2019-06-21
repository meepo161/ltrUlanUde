package ru.avem.posum.controllers.protocol

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import javafx.collections.ObservableList
import okio.BufferedSource
import okio.Okio
import ru.avem.posum.controllers.process.ProcessController
import ru.avem.posum.models.process.ChannelModel
import ru.avem.posum.models.protocol.ChannelDataModel
import java.io.File
import java.io.FileNotFoundException
import java.text.SimpleDateFormat

class JsonControllerTwo(private val processController: ProcessController) {
    private val file = File(System.getProperty("user.dir") + "\\json.txt")
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val channelsDataAdapter = moshi.adapter(ChannelDataModel::class.java)
    private var linesCount = 0
    private val chosenParameters = arrayOf(false, false, false)

    fun createJson() {
        try {
            file.writeText("") // beginning of list
        } catch (e: FileNotFoundException) {
            file.createNewFile()
        }

    }

    fun write(channelsModels: ObservableList<ChannelModel>) {
        val writerThread = Thread {
            for (channelModel in channelsModels) {
                val channelData = ChannelDataModel(channelModel)
                val json = channelsDataAdapter.toJson(channelData)
                file.appendText("$json\n")
            }
        }
        writerThread.priority = 10
        writerThread.start()
    }


    fun parse(aCopy: Boolean, rarefactionCoefficient: Long) {
        estimateJson(aCopy)
        val reader = getReader(aCopy)

        var firstTime: Long = 0
        while (!reader.exhausted()) {
            val line: String = reader.readUtf8Line() ?: break
            val json = if (line.first() != '{') line.substring(1) else line
            val channelDataModel = channelsDataAdapter.fromJson(json)
            val simpleTimeFormat = SimpleDateFormat("HH:mm:ss")
            val time = simpleTimeFormat.parse(channelDataModel!!.getTime()).time
            if (firstTime == 0.toLong()) firstTime = time
            val isFirstValues = simpleTimeFormat.parse(channelDataModel.getTime()).time == firstTime
            val doNotThinOut = (rarefactionCoefficient == 1000.toLong())
            val thinnedChannelDataModel = simpleTimeFormat.parse(channelDataModel.getTime()).time == (firstTime + rarefactionCoefficient)

            if (isFirstValues || doNotThinOut || thinnedChannelDataModel) {
                processController.protocolController.fillChannelData(channelDataModel.toList(chosenParameters))
            }
        }
    }

    private fun estimateJson(aCopy: Boolean) {
        val reader = getReader(aCopy)
        var lines = 0
        while (!reader.exhausted()) {
            lines++
            val line: String = reader.readUtf8Line() ?: break
            val json = if (line.first() != '{') line.substring(1) else line
            val channelDataModel = channelsDataAdapter.fromJson(json)
            val chosenParameterIndex = channelDataModel!!.chosenParameterIndex.toInt()
            if (chosenParameterIndex != -1) { chosenParameters[chosenParameterIndex] = true}
        }
        linesCount = lines
    }

    private fun getReader(aCopy: Boolean): BufferedSource {
        return if (aCopy) {
            Okio.buffer(Okio.source(File("${file.path}.tmp")))
        } else {
            Okio.buffer(Okio.source(file))
        }
    }

    fun copy() {
        val json = file.readText()
        val temp = File("${file.path}.tmp")
        temp.createNewFile()
        temp.writeText(json)
    }
}