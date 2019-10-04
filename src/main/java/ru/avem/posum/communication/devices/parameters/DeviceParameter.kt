package ru.avem.posum.communication.devices.parameters

import ru.avem.posum.communication.devices.Parameter
import ru.avem.posum.communication.devices.enums.DeviceType

class DeviceParameter(val unitID: Int, val device: DeviceType, val parameter: Parameter, value: Number) {
    val value = value.toDouble()

    init {
        require(device.parameter == parameter.javaClass) { "${device.parameter} != ${parameter.javaClass}" }
    }
}
