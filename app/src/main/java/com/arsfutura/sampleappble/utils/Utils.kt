package com.arsfutura.sampleappble.utils

import java.util.*

private val BLUETOOTH_BASE_UUID = UUID.fromString("00000000-0000-1000-8000-00805F9B34FB")

fun Long.toBleUuid(): UUID = UUID(BLUETOOTH_BASE_UUID.mostSignificantBits.plus(this shl 32), BLUETOOTH_BASE_UUID.leastSignificantBits)

fun ByteArray.toHexString() = this.joinToString("") { String.format("%02X", (it.toInt() and 0xFF)) }

/**
 * Byte 0x00 - true
 * Byte 0x01 - false
 */
fun Byte.toBoolean(): Boolean = this == 0x00.toByte()
fun Boolean.toByte(): Byte = if (this) 0x00 else 0x01