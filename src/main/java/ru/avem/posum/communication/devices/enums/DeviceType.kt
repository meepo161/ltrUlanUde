package ru.avem.posum.communication.devices.enums

import ru.avem.posum.communication.devices.Parameter
import ru.avem.posum.communication.devices.mu110.OwenMU110Controller

enum class DeviceType(val parameter: Class<out Parameter>) {
    MU110(OwenMU110Controller.Parameters::class.java)
}
