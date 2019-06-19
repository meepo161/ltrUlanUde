package ru.avem.posum.controllers.protocol

enum class ProtocolSheets(val sheetName: String) {
    GENERAL_DESCRIPTION("Общие данне"), CHANNELS_DATA("Нагрузка на каналах"),
    JOURNAL("Журнал событий"), COMMANDS("Программа испытаний")
}