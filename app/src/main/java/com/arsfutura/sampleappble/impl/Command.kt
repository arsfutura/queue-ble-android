package com.arsfutura.sampleappble.impl

import com.arsfutura.sampleappble.ble.BluetoothModule
import com.arsfutura.sampleappble.utils.toBoolean
import com.arsfutura.sampleappble.utils.toByte
import java.util.*

interface Command {

    suspend fun execute()
}

class ConnectCommand(private val module: BluetoothModule) : Command {

    override suspend fun execute() = module.connect()

    override fun toString() = "ConnectCommand"
}

class DisconnectCommand(private val module: BluetoothModule) : Command {

    override suspend fun execute() = module.disconnect()

    override fun toString() = "DisconnectCommand"
}

class ReadCommand(private val module: BluetoothModule,
                  private val serviceUUID: UUID,
                  private val characteristicUUID: UUID) : Command {

    lateinit var readResult: ByteArray

    override suspend fun execute() {
        readResult = module.read(serviceUUID, characteristicUUID)
    }

    override fun toString() = "ReadCommand"
}

class WriteCommand(private val module: BluetoothModule,
                   private val serviceUUID: UUID,
                   private val characteristicUUID: UUID,
                   private val bytes: ByteArray) : Command {

    override suspend fun execute() = module.write(serviceUUID, characteristicUUID, bytes)

    override fun toString() = "WriteCommand"
}

class ToggleCommand(private val module: BluetoothModule,
                    private val serviceUUID: UUID,
                    private val characteristicUUID: UUID) : Command {

    override suspend fun execute() {
        val enabled = module.read(serviceUUID, characteristicUUID)[0].toBoolean()
        module.write(serviceUUID, characteristicUUID, byteArrayOf(enabled.not().toByte()))
    }

    override fun toString(): String = "ToggleLightsCommand"
}