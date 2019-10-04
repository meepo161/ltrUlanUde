package ru.avem.posum.communication

import ru.avem.posum.communication.ModbusConnection.isAppRunning
import ru.avem.posum.communication.ModbusConnection.isModbusConnected
import ru.avem.posum.communication.devices.Device
import ru.avem.posum.communication.devices.enums.DeviceType
import ru.avem.posum.communication.devices.enums.UnitID
import ru.avem.posum.communication.devices.parameters.DeviceParameter
import ru.avem.posum.communication.devices.mu110.OwenMU110Controller
import java.lang.Thread.sleep
import java.util.*

object CommunicationModel : Observer {
    var mU110Controller = OwenMU110Controller(UnitID.MU110, this)

    private var register: Double = 0.0

    val deviceControllers = listOf<Device>(mU110Controller)

    init {
        Thread {
            while (isAppRunning) {
                deviceControllers.forEach {
                    if (isModbusConnected) {
                        try {
                            when (it) {
                                is OwenMU110Controller -> {
                                    try {
                                        it.readRegister()
                                        it.on1KM()
                                        it.isResponding = true
                                    } catch (e: Exception) {
                                        it.isResponding = false
                                    }
                                }
                            }
                        } catch (e: NullPointerException) {
                        }
                    }
                }
                sleep(1)
            }
        }.start()
    }

    override fun update(o: Observable?, arg: Any?) {
        arg as DeviceParameter
        val value = arg.value
        when (arg.device) {
            DeviceType.MU110 -> {
                when (arg.parameter) {
                    OwenMU110Controller.Parameters.REGISTER_OUT -> register = value
                }
            }
        }
    }
}
