package ru.avem.posum.controllers.protocol

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
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

    fun load(): List<ChannelDataModel>? {
        val listType = Types.newParameterizedType(List::class.java, ChannelDataModel::class.java)
        val jsonAdapter: JsonAdapter<List<ChannelDataModel>> = moshi.adapter(listType)
        val json = File(path).readText()
        return jsonAdapter.fromJson(json)
    }

    fun close() {
        val json = File(path).readText().removeSuffix(",\n")
        File(path).writeText("$json\n]")
    } // end of list
}