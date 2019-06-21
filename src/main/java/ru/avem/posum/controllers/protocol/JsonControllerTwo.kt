package ru.avem.posum.controllers.protocol

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import javafx.collections.ObservableList
import okio.Okio
import ru.avem.posum.controllers.process.ProcessController
import ru.avem.posum.models.process.ChannelModel
import ru.avem.posum.models.protocol.ChannelDataModel
import java.io.File
import java.io.FileNotFoundException

class JsonControllerTwo(private val processController: ProcessController) {
    private val file = File(System.getProperty("user.dir") + "\\json.txt")
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val channelsDataAdapter = moshi.adapter(ChannelDataModel::class.java)

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

    fun parse(aCopy: Boolean) {
        val bufferedSource = if (aCopy) {
            Okio.buffer(Okio.source(File("${file.path}.tmp")))
        } else {
            Okio.buffer(Okio.source(file))
        }

        while (!bufferedSource.exhausted()) {
            val line: String = bufferedSource.readUtf8Line() ?: break
            val json = if (line.first() != '{') line.substring(1) else line
            val channelDataModel = channelsDataAdapter.fromJson(json)
            processController.protocolController.fillChannelData(channelDataModel!!.toList())
        }
    }

    fun copy() {
        val json = file.readText()
        val temp = File("${file.path}.tmp")
        temp.createNewFile()
        temp.writeText(json)
    }
}