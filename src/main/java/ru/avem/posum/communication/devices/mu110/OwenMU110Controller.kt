package ru.avem.posum.communication.devices.mu110

import com.ucicke.k2mod.modbus.ModbusException
import com.ucicke.k2mod.modbus.procimg.SimpleRegister
import org.slf4j.LoggerFactory
import ru.avem.posum.communication.ModbusConnection
import ru.avem.posum.communication.devices.Device
import ru.avem.posum.communication.devices.Parameter
import ru.avem.posum.communication.devices.enums.DeviceType
import ru.avem.posum.communication.devices.enums.UnitID
import ru.avem.posum.communication.devices.parameters.DeviceParameter
import ru.avem.posum.utils.Utils.sleep
import java.util.*
import kotlin.concurrent.thread
import kotlin.math.pow

class OwenMU110Controller(private val unitID: UnitID, observer: Observer) : Observable(), Device {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)

        const val REGISTER = 0

        val DEVICE_ID = DeviceType.MU110

        var isNeedMotor = false
        var mask = 0
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

    fun onRegisterInTheKms(kms: Int) {
        when (kms) {
            1 -> mask = mask or 1
            2 -> mask = mask or 2
            3 -> mask = mask or 4
            4 -> mask = mask or 8
            5 -> mask = mask or 16
            6 -> mask = mask or 32
            7 -> mask = mask or 64
            8 -> mask = mask or 128
        }
        onKM(mask.toShort())
    }

    fun offRegisterInTheKms(kms: Int) {
        when (kms) {
            1 -> mask = mask xor 1
            2 -> mask = mask xor 2
            3 -> mask = mask xor 4
            4 -> mask = mask xor 8
            5 -> mask = mask xor 16
            6 -> mask = mask xor 32
            7 -> mask = mask xor 64
            8 -> mask = mask xor 128
        }
        onKM(mask.toShort())
    }

    fun onKM(mask: Short) {
        try {
            ModbusConnection.master!!.writeMultipleRegisters(unitID.id, 50, arrayOf(SimpleRegister(mask)))
        } catch (e: Exception) {
            isResponding = false
        }
    }

    fun offAllKms() {
        isNeedMotor = false
        ModbusConnection.master!!.writeMultipleRegisters(unitID.id, 0, arrayOf(SimpleRegister(1.toShort())))
    }

    fun onKM1() {
        onRegisterInTheKms(1)
    }

    fun onKM2() {
        onRegisterInTheKms(2)
    }

    fun onKM3() {
        onRegisterInTheKms(3)
    }

    fun onKM4() {
        onRegisterInTheKms(4)
    }

    fun onKM5() {
        onRegisterInTheKms(5)
    }

    fun onKM6() {
        onRegisterInTheKms(6)
    }

    fun onKM7() {
        onRegisterInTheKms(7)
    }

    fun onKM8() {
        onRegisterInTheKms(8)
    }

    fun offKM1() {
        isNeedMotor = false
        offRegisterInTheKms(1)
    }

    fun offKM2() {
        offRegisterInTheKms(2)
    }

    fun offKM3() {
        offRegisterInTheKms(3)
    }

    fun offKM4() {
        offRegisterInTheKms(4)
    }

    fun offKM5() {
        offRegisterInTheKms(5)
    }

    fun offKM6() {
        offRegisterInTheKms(6)
    }

    fun offKM7() {
        offRegisterInTheKms(7)
    }

    fun offKM8() {
        offRegisterInTheKms(8)
    }


    private fun notice(parameter: DeviceParameter) {
        setChanged()
        notifyObservers(parameter)
    }
}
