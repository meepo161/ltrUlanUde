package ru.avem.posum.models.process

/**
 * Перечисление видов запланированных команд
 */

enum class CommandsTypes(val typeName: String) {
    PAUSE("Пауза"), STOP("Стоп");
}