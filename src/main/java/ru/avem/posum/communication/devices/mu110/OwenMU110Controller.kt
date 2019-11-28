package ru.avem.posum.communication.devices.mu110

import com.ucicke.k2mod.modbus.ModbusException
import com.ucicke.k2mod.modbus.procimg.Register
import com.ucicke.k2mod.modbus.procimg.SimpleRegister
import org.slf4j.LoggerFactory
import ru.avem.posum.communication.ModbusConnection
import ru.avem.posum.communication.devices.Device
import ru.avem.posum.communication.devices.Parameter
import ru.avem.posum.communication.devices.enums.DeviceType
import ru.avem.posum.communication.devices.enums.UnitID
import ru.avem.posum.communication.devices.parameters.DeviceParameter
import java.util.*
import kotlin.concurrent.thread
import kotlin.system.exitProcess

class OwenMU110Controller(private val unitID: UnitID, observer: Observer) : Observable(), Device {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)

        const val REGISTER = 0

        val DEVICE_ID = DeviceType.MU110

        var isNeedMotor = false
    }

    enum class Parameters : Parameter {
        IS_RESPONDING,
        REGISTER_OUT
    }

    override var isResponding = false
        set(value) {
            field = value
            notice(DeviceParameter(unitID.id, DEVICE_ID, Parameters.IS_RESPONDING, if (field) 1 else 0))
        }

    init {
        addObserver(observer)
    }

    fun readRegister() {
        try {
            val readInputRegisters = ModbusConnection.readInputRegisters(unitID.id, REGISTER, 1)
            val registerValue = readInputRegisters.first().value
            logger.debug("Register value: {}", registerValue.toString())
            notice(DeviceParameter(unitID.id, DEVICE_ID, Parameters.REGISTER_OUT, registerValue))
        } catch (e: ModbusException) {
            isResponding = false
        }
    }

    fun on1KM() {
        try {
            isNeedMotor = true;
            thread {
                while (isNeedMotor) {
                    ModbusConnection.master!!.writeMultipleRegisters(unitID.id, 50, arrayOf(SimpleRegister(1.toShort())))
                }
            }
        } catch (e: Exception) {
            isResponding = false
        }
    }

    fun off1KM() {
        isNeedMotor = false
    }


    private fun notice(parameter: DeviceParameter) {
        setChanged()
        notifyObservers(parameter)
    }
}
