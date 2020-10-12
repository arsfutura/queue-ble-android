package com.arsfutura.sampleappble.ble

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import android.util.Log
import com.beepiz.bluetooth.gattcoroutines.ExperimentalBleGattCoroutinesCoroutinesApi
import com.beepiz.bluetooth.gattcoroutines.GattConnection
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.delay
import java.util.*

@ExperimentalBleGattCoroutinesCoroutinesApi
@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class BluetoothModuleImpl(private val bluetoothDevice: BluetoothDevice) : BluetoothModule {

    private var connection = GattConnection(bluetoothDevice, GattConnection.ConnectionSettings(autoConnect = false, transport = BluetoothDevice.TRANSPORT_LE))

    companion object {
        val TAG: String = BluetoothModuleImpl::class.java.simpleName
    }

    override suspend fun connect() {
        if (connection.isConnected) {
            Log.w(TAG, "Device already connected!")
        } else {
            Log.d(TAG, "Device connecting...")
            connection = GattConnection(bluetoothDevice, GattConnection.ConnectionSettings(autoConnect = false, transport = BluetoothDevice.TRANSPORT_LE))
            connection.connect()
            delay(1600)
            connection.discoverServices()
            Log.i(TAG, "Device connected.")
            connection.requestMtu(512)
        }
    }

    override suspend fun disconnect() {
        if (connection.isConnected) {
            Log.d(TAG, "Device closing connection...")
            connection.close()
            delay(200)
            Log.i(TAG, "Device disconnected.")
        } else {
            Log.w(TAG, "Device already disconnected!")
        }
    }

    override suspend fun read(serviceUUID: UUID, characteristicUUID: UUID): ByteArray {
        val characteristic = getCharacteristic(serviceUUID, characteristicUUID)
        return connection.readCharacteristic(characteristic).value
    }

    override suspend fun write(serviceUUID: UUID, characteristicUUID: UUID, value: ByteArray) {
        val characteristic = getCharacteristic(serviceUUID, characteristicUUID)
        characteristic.value = value
        connection.writeCharacteristic(characteristic)
    }

    private fun getCharacteristic(serviceUUID: UUID, characteristicUUID: UUID): BluetoothGattCharacteristic {
        val service = connection.getService(serviceUUID)
                ?: throw IllegalStateException("Service with UUID: $serviceUUID doesn't exist!")
        return service.getCharacteristic(characteristicUUID)
                ?: throw IllegalStateException("Characteristic with UUID: $characteristicUUID doesn't exist!")
    }
}