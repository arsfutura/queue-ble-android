package com.arsfutura.sampleappble.ble

import java.util.*

interface BluetoothModule {

    suspend fun connect()

    suspend fun disconnect()

    suspend fun read(serviceUUID: UUID, characteristicUUID: UUID): ByteArray

    suspend fun write(serviceUUID: UUID, characteristicUUID: UUID, value: ByteArray)
}