package com.arsfutura.sampleappble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.arsfutura.sampleappble.ble.BluetoothModule
import com.arsfutura.sampleappble.ble.BluetoothModuleImpl
import com.arsfutura.sampleappble.impl.*
import com.arsfutura.sampleappble.utils.toBleUuid
import com.arsfutura.sampleappble.utils.toBoolean
import com.arsfutura.sampleappble.utils.toHexString
import com.beepiz.bluetooth.gattcoroutines.ExperimentalBleGattCoroutinesCoroutinesApi
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
@ExperimentalBleGattCoroutinesCoroutinesApi
@ObsoleteCoroutinesApi
class MainActivity : AppCompatActivity(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext = job + Dispatchers.Main

    companion object {
        val TAG: String = MainActivity::class.java.simpleName

        // TODO change device address and services/characteristics UUIDs
        const val DEVICE_ADDRESS = "68:5A:CF:92:07:4B"
        private val GENERIC_ACCESS_SERVICE_UUID = 0x1800.toLong().toBleUuid()
        private val DEVICE_NAME_CHARACTERISTIC_UUID = 0x2A00.toLong().toBleUuid()

        private val CURRENT_TIME_SERVICE_UUID = 0x1805.toLong().toBleUuid()
        private val CURRENT_TIME_CHARACTERISTIC_UUID = 0x2A2B.toLong().toBleUuid()

        private val LIGHT_SERVICE_UUID = UUID.fromString("11111111-2222-3333-4444-555555555555")
        private val LIGHT_CHARACTERISTIC_UUID = UUID.fromString("AAAAAAAA-BBBB-CCCC-DDDD-EEEEEEEEEEEE")
    }

    private lateinit var bluetoothDevice: BluetoothDevice
    private lateinit var bluetoothModule: BluetoothModule
    private lateinit var commandQueue: CommandQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bluetoothDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(DEVICE_ADDRESS)
        bluetoothModule = BluetoothModuleImpl(bluetoothDevice)
        commandQueue = CommandQueueImpl(this)

        findViewById<AppCompatButton>(R.id.button).setOnClickListener { runCommands() }
    }

    private fun runCommands() = launch {
        commandQueue.enqueue(ConnectCommand(bluetoothModule))

        val genericReadCommand = ReadCommand(bluetoothModule, GENERIC_ACCESS_SERVICE_UUID, DEVICE_NAME_CHARACTERISTIC_UUID)
        commandQueue.enqueue(genericReadCommand)
        Log.i(TAG, "Generic Access Info: ${String(genericReadCommand.readResult)}")

        val currentTimeReadCommand = ReadCommand(bluetoothModule, CURRENT_TIME_SERVICE_UUID, CURRENT_TIME_CHARACTERISTIC_UUID)
        commandQueue.enqueue(currentTimeReadCommand)
        Log.i(TAG, "Current time: ${currentTimeReadCommand.readResult.toHexString()}")

        val readLightsCommand = ReadCommand(bluetoothModule, LIGHT_SERVICE_UUID, LIGHT_CHARACTERISTIC_UUID)
        commandQueue.enqueue(readLightsCommand)
        Log.e(TAG, "Lights Enabled: ${readLightsCommand.readResult[0].toBoolean()}")

        val toggleLightsCommand = ToggleCommand(bluetoothModule, LIGHT_SERVICE_UUID, LIGHT_CHARACTERISTIC_UUID)
        commandQueue.enqueue(toggleLightsCommand)

        commandQueue.enqueue(readLightsCommand)
        Log.e(TAG, "Lights Enabled: ${readLightsCommand.readResult[0].toBoolean()}")

        commandQueue.enqueue(DisconnectCommand(bluetoothModule))
    }
}