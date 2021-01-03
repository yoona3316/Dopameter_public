package com.example.dopameter.tracker

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.example.dopameter.Utils
import com.example.dopameter.model.sensors.BluetoothListAdapter
import com.example.dopameter.model.sensors.BluetoothModel
import java.lang.IllegalArgumentException

// Not Used for now.
class BluetoothTracker(val context: Context): Tracker() {
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)

    private val receiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    val rssi = intent.getShortExtra(
                        BluetoothDevice.EXTRA_RSSI,
                        Short.MIN_VALUE
                    ).toInt()
                    BluetoothListAdapter.add(
                        BluetoothModel(
                            Utils.getUnixNow(), true, device?.address, device?.type,
                            device?.bluetoothClass?.deviceClass, rssi, device?.name
                        )
                    )
                }
            }
        }
    }
    override val TAG: String
        get() = BluetoothTracker::class.java.simpleName

    override fun start() {
        super.start()
        bluetoothAdapter?.run{
            context.registerReceiver(receiver, filter)
        }
    }

    override fun stop() {
        super.stop()
        bluetoothAdapter?.run{
            try {
                context.unregisterReceiver(receiver)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
        }
    }

    fun run() {
        if (bluetoothAdapter?.isEnabled==true)
            bluetoothAdapter.startDiscovery()
        else {
            BluetoothListAdapter.add(
                BluetoothModel(
                    Utils.getUnixNow(), false, null, null,
                    null, null, null
                )
            )
        }
    }
}