package com.example.dopameter.tracker

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.util.Log
import com.example.dopameter.Utils
import com.example.dopameter.model.sensors.BleListAdapter
import com.example.dopameter.model.sensors.BleModel

class BleTracker(val context: Context, val scan_period: Long): Tracker() {
    override val TAG: String
        get() = BleTracker::class.java.simpleName

    private val bluetoothManager: BluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter = bluetoothManager.adapter
    private val handler: Handler = Handler()
    private val bluetoothLeScanner: BluetoothLeScanner? = bluetoothAdapter.bluetoothLeScanner

    private var bleListAdapter: BleListAdapter =
        BleListAdapter

    private var usable = true
    private var mScanning = false

    private val scanCallback: ScanCallback = object: ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            result?.device ?: return
            bleListAdapter.add(
                BleModel(
                    Utils.getUnixNow(), result.timestampNanos, result.device.address,
                    result.device.bluetoothClass.deviceClass, result.rssi, Utils.getHash(result.device.name)
                )
            )
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.d(TAG, "Failed to scan bluetooth")
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
            results?.size ?: return
            for (item in results) {
                bleListAdapter.add(
                    BleModel(
                        Utils.getUnixNow(), item.timestampNanos, item.device.address,
                        item.device.bluetoothClass.deviceClass, item.rssi, Utils.getHash(item.device.name)
                    )
                )
            }
        }
    }

//    TODO(may need refactoring)
    override fun start() {
        super.start()
        context.packageManager.takeIf { it.missingSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) }?.also{
            usable = false
        }
        if (!usable) return

        scanLeDevice(bluetoothAdapter.isEnabled)
    }

    override fun stop() {
        super.stop()
        if (!usable) return
        mScanning = false
        bluetoothLeScanner?.stopScan(scanCallback)
    }

    private fun PackageManager.missingSystemFeature(name: String): Boolean = !hasSystemFeature(name)

    private fun scanLeDevice(enable: Boolean) {
        when (enable) {
            true -> {
                handler.postDelayed({
                    mScanning = false
                    bluetoothLeScanner?.stopScan(scanCallback)
                }, scan_period)
                mScanning = true
                bluetoothLeScanner?.startScan(scanCallback)
            }
            false -> {
                mScanning = false
                bluetoothLeScanner?.stopScan(scanCallback)
            }
        }
    }

}