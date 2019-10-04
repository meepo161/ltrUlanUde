package ru.avem.posum.communication

import com.fazecast.jSerialComm.SerialPort
import com.fazecast.jSerialComm.SerialPortInvalidPortException
import com.ucicke.k2mod.modbus.ModbusIOException
import com.ucicke.k2mod.modbus.facade.ModbusSerialMaster
import com.ucicke.k2mod.modbus.procimg.InputRegister
import com.ucicke.k2mod.modbus.procimg.Register
import com.ucicke.k2mod.modbus.util.SerialParameters
import org.slf4j.LoggerFactory
import ru.avem.posum.communication.devices.enums.COMState
import java.lang.Thread.sleep

object ModbusConnection {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val MUTEX = Any()

    var master: ModbusSerialMaster? = null
    var isAppRunning = true
        set(value) {
            field = value
            isModbusConnected = field
        }
    var isModbusConnected = false
        set(value) {
            field = value
            if (value) {
                indicateCOM(COMState.CONNECTING)
            }
        }

    var indicateCOM: ((COMState) -> Unit) = { COMState.CLOSE }

    init {
        Thread {
            while (isAppRunning) {
                if (!isSerialConnecting()) {
                    isModbusConnected = try {
                        initModbusConnection()
                    } catch (e: Exception) {
                        false
                    }
                }

                if (!isSerialConnecting()) {
                    CommunicationModel.deviceControllers.forEach {
                        it.isResponding = false
                    }
                }

                sleep(100)
                indicateCOM(if (isSerialConnecting()) COMState.OPEN else COMState.CLOSE)
                sleep(100)
            }
        }.start()
    }

    private fun isSerialConnecting(): Boolean {
        return isModbusConnected && master != null && master!!.connection != null && master!!.connection.isOpen && master!!.connection.bytesAvailable() >= 0
    }

    fun initModbusConnection(): Boolean {
        val serialParams = SerialParameters()

        val cp2103 = try {
            detectInterfaceConverter()
        } catch (e: SerialPortInvalidPortException) {
            logger.error("Не подключен преобразователь RS-485 -> USB.")
            return false
        }

        if (cp2103 != null) {
            serialParams.portName = cp2103.systemPortName
            serialParams.encoding = ENCODING
            serialParams.baudRate = BAUD_RATE
            serialParams.databits = DATABITS
            serialParams.parity = PARITY
            serialParams.stopbits = STOPBITS
        }

        master = ModbusSerialMaster(serialParams, 300)
        master!!.connect()
        master!!.setRetries(2)

        return master!!.connection.isOpen
    }

    fun closeConnection(): Boolean {
        if (master != null && master!!.connection.isOpen) {
            master!!.disconnect()
        }

        return master?.connection?.isOpen ?: false
    }

    fun detectInterfaceConverter(): SerialPort? {
        val filter = SerialPort.getCommPorts().filter { it.portDescription == CONVERTER_NAME }

        if (filter.isNullOrEmpty()) {
            throw SerialPortInvalidPortException("Cannot find CP2103 converter.")
        } else {
            return filter.first()
        }
    }

    fun writeSingleRegister(unitID: Int, ref: Int, reg: Register, bytesInRegister: Int = 2) {
        if (master != null) {
            synchronized(MUTEX) {
                master!!.writeSingleRegister(unitID, ref, reg, bytesInRegister)
            }
        } else {
            throw ModbusIOException()
        }
    }

    fun readInputRegisters(unitID: Int, ref: Int, count: Int, bytesInRegister: Int = 2): Array<out InputRegister> {
        if (master != null) {
            synchronized(MUTEX) {
                return master!!.readInputRegisters(unitID, ref, count, bytesInRegister)
            }
        } else {
            throw ModbusIOException()
        }
    }
}
