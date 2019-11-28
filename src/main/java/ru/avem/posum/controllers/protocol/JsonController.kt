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
import java.text.SimpleDateFormat

class JsonController(private val processController: ProcessController) {
    private var channelsCount = 0
    private val file = File(System.getProperty("user.dir") + "\\json.txt")
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val channelsDataAdapter = moshi.adapter(ChannelDataModel::class.java)
    private var lastTime: Long = 0
    private var linesCount = 0
    private val chosenParameters = arrayOf(false, false, false)

    // Создает файл Json
    fun createJson() {
        file.writeText("") // beginning of list
    }

    // Сериализует модель и записывает в Json
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

    // Считывает модели из Json с заданным прореживанием
    fun parseFullFile(rarefactionCoefficient: Long) {
        estimateJson(false)
        val reader = getReader(false)

        var firstTime: Long = 0
        var rarefactions = 0
        var iterations = 0
        while (!reader.exhausted()) {
            iterations++
            val line: String = reader.readUtf8Line() ?: break
            val json = if (line.first() != '{') line.substring(1) else line
            val channelDataModel = channelsDataAdapter.fromJson(json)
            val simpleTimeFormat = SimpleDateFormat("HH:mm:ss")
            val time = simpleTimeFormat.parse(channelDataModel!!.getTime()).time
            if (firstTime == 0.toLong()) firstTime = time
            val isFirstValues = simpleTimeFormat.parse(channelDataModel.getTime()).time == firstTime
            val doNotThinOut = (rarefactionCoefficient == 1000.toLong())
            val thinnedChannelDataModel = time == (firstTime + rarefactionCoefficient * rarefactions)
            if ((iterations % channelsCount == 0) && ((time - firstTime) % rarefactionCoefficient == 0.toLong())) {
                rarefactions++
            }

            if (isFirstValues || doNotThinOut || thinnedChannelDataModel) {
                processController.protocolController.fillChannelData(channelDataModel.toList(chosenParameters))
            }
        }
        channelsCount = 0
    }

    private fun getReader(aCopy: Boolean): BufferedSource {
        return if (aCopy) {
            Okio.buffer(Okio.source(File("${file.path}.tmp")))
        } else {
            Okio.buffer(Okio.source(file))
        }
    }

    // Оценивает файл Json
    private fun estimateJson(aCopy: Boolean) {
        val reader = getReader(aCopy)
        val simpleTimeFormat = SimpleDateFormat("HH:mm:ss")
        var lines = 0
        var firstTime: Long = 0
        var time: Long = 0

        while (!reader.exhausted()) {
            val line: String = reader.readUtf8Line() ?: break
            val json = if (line.first() != '{') line.substring(1) else line
            val channelDataModel = channelsDataAdapter.fromJson(json)
            val chosenParameterIndex = channelDataModel!!.chosenParameterIndex.toInt()
            if (chosenParameterIndex != -1) {
                chosenParameters[chosenParameterIndex] = true
            }
            time = simpleTimeFormat.parse(channelsDataAdapter.fromJson(json)?.getTime()).time
            if (firstTime == 0.toLong()) {
                firstTime = time
            }
            if (time == firstTime) {
                channelsCount++
            }
            lines++
        }
        lastTime = time
        linesCount = lines
    }

    // Считывает модели за последние 60 секунд процесса испытаний
    fun parsePieceOfFile() {
        copy()
        val reader = getReader(true)
        estimateJson(true)
        val simpleTimeFormat = SimpleDateFormat("HH:mm:ss")
        val timeLimit: Long = lastTime - 60_000

        while (!reader.exhausted()) {
            val line: String = reader.readUtf8Line() ?: break
            val json = if (line.first() != '{') line.substring(1) else line
            val channelDataModel = channelsDataAdapter.fromJson(json)
            val time = simpleTimeFormat.parse(channelDataModel!!.getTime()).time
            val isLastValues = time in timeLimit until lastTime

            if (isLastValues) {
                processController.protocolController.fillChannelData(channelDataModel.toList(chosenParameters))
            }
        }
    }

    // Считывает модели за последнюю секунду процесса испытаний
    fun parseOneSecond() {
        copy()
        val reader = getReader(true)
        estimateJson(true)
        val simpleTimeFormat = SimpleDateFormat("HH:mm:ss")

        while (!reader.exhausted()) {
            val line: String = reader.readUtf8Line() ?: break
            val json = if (line.first() != '{') line.substring(1) else line
            val channelDataModel = channelsDataAdapter.fromJson(json)
            val time = simpleTimeFormat.parse(channelDataModel!!.getTime()).time

            if (time == lastTime) {
                processController.protocolController.fillChannelData(channelDataModel.toList(chosenParameters))
            }
        }
    }

    // Копирует Json в промежуточный файл
    fun copy() {
        val json = file.readText()
        val temp = File("${file.path}.tmp")
        temp.createNewFile()
        temp.writeText(json)
    }
}