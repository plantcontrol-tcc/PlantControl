package com.plantcontrol.app.data.remote

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

class BluetoothService {
    private var socket: BluetoothSocket? = null
    private var outStream: OutputStream? = null
    private var inStream: InputStream? = null
    private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    @SuppressLint("MissingPermission")
    suspend fun connect(device: BluetoothDevice): Boolean = withContext(Dispatchers.IO) {
        try {
            socket = device.createRfcommSocketToServiceRecord(SPP_UUID)
            socket?.connect()
            outStream = socket?.outputStream
            inStream = socket?.inputStream
            true
        } catch (e: Exception) {
            disconnect()
            false
        }
    }

    suspend fun sendCommand(command: String) = withContext(Dispatchers.IO) {
        try {
            outStream?.write((command + "\n").toByteArray())
            outStream?.flush()
        } catch (_: Exception) {}
    }

    fun disconnect() {
        try { socket?.close() } catch (_: Exception) {}
        socket = null
    }
}