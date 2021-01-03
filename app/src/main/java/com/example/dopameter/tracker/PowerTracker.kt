package com.example.dopameter.tracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.example.dopameter.Utils
import com.example.dopameter.model.sensors.Charged
import com.example.dopameter.model.sensors.Docked
import com.example.dopameter.model.sensors.PowerListAdapter
import com.example.dopameter.model.sensors.PowerModel
import java.lang.IllegalArgumentException

class PowerTracker(val context: Context): Tracker() {
    override val TAG: String = PowerTracker::class.java.simpleName
    private val powerFilter = IntentFilter()
    private val powerReceiver = PowerReceiver()
    override fun start() {
        super.start()

        powerFilter.addAction(Intent.ACTION_BATTERY_LOW)
        powerFilter.addAction(Intent.ACTION_BATTERY_OKAY)
        powerFilter.addAction(Intent.ACTION_BATTERY_CHANGED)
        powerFilter.addAction(Intent.ACTION_DOCK_EVENT)

        context.registerReceiver(powerReceiver, powerFilter)
    }

    override fun stop() {
        super.stop()
        try {
            context.unregisterReceiver(powerReceiver)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }
    class PowerReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_BATTERY_CHANGED -> {
                    onBatteryChanged(intent)
                }
                Intent.ACTION_BATTERY_OKAY -> {

                }
                Intent.ACTION_BATTERY_LOW -> {

                }
                Intent.ACTION_DOCK_EVENT -> {
                    onBatteryDocked(intent)
                }
            }
        }
        fun onBatteryDocked(intent: Intent?) {
            intent ?: return
            val dockState: Int = intent.getIntExtra(Intent.EXTRA_DOCK_STATE, -1) ?: -1
            val isDocked: Boolean = dockState != Intent.EXTRA_DOCK_STATE_UNDOCKED
            val isCar: Boolean = dockState == Intent.EXTRA_DOCK_STATE_CAR
            val isDesk: Boolean = dockState == Intent.EXTRA_DOCK_STATE_DESK
                    || dockState == Intent.EXTRA_DOCK_STATE_LE_DESK
                    || dockState == Intent.EXTRA_DOCK_STATE_HE_DESK
            PowerListAdapter.add(
                PowerModel(
                    Utils.getUnixNow(),
                    null,
                    Docked(
                        isDocked,
                        isCar,
                        isDesk
                    )
                )
            )
        }
        fun onBatteryChanged(intent: Intent?) {
            intent ?: return
//            Plug: BATTERY, AC, USB, WIRELESS
            val plug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)

//            Battery status: CHARGING, DISCHARGING, FULL, NOT_CHARGING, UNKNOWN
            val batteryStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN)

//            Battery level
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val batteryPct: Float? = level * 100 / scale.toFloat()
            PowerListAdapter.add(
                PowerModel(
                    Utils.getUnixNow(),
                    Charged(
                        plug,
                        batteryStatus,
                        level,
                        scale,
                        batteryPct
                    ), null
                )
            )
        }

    }
}