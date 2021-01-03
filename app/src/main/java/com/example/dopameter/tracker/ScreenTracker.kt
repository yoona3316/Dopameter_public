package com.example.dopameter.tracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.example.dopameter.Utils
import com.example.dopameter.model.sensors.ScreenListAdapter
import com.example.dopameter.model.sensors.ScreenLockListAdapter
import com.example.dopameter.model.sensors.ScreenLockModel
import com.example.dopameter.model.sensors.ScreenModel

class ScreenTracker(val context: Context): Tracker() {
    override val TAG: String
        get() = super.TAG
    private val filter: IntentFilter = IntentFilter().also {
        it.addAction(Intent.ACTION_SCREEN_ON)
        it.addAction(Intent.ACTION_SCREEN_OFF)
        it.addAction(Intent.ACTION_USER_PRESENT)
    }
    private val receiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_SCREEN_ON -> {
                    ScreenListAdapter.add(
                        ScreenModel(
                            Utils.getUnixNow(),
                            ScreenModel.SCREEN_ON
                        )
                    )
                }
                Intent.ACTION_SCREEN_OFF -> {
                    ScreenListAdapter.add(
                        ScreenModel(
                            Utils.getUnixNow(),
                            ScreenModel.SCREEN_OFF
                        )
                    )
                    ScreenLockListAdapter.add(
                        ScreenLockModel(
                            Utils.getUnixNow(),
                            ScreenLockModel.SCREEN_LOCK
                        )
                    )
                }
                Intent.ACTION_USER_PRESENT -> {
                    ScreenLockListAdapter.add(
                        ScreenLockModel(
                            Utils.getUnixNow(),
                            ScreenLockModel.SCREEN_UNLOCK
                        )
                    )
                }
            }
        }
    }

    override fun start() {
        super.start()
        context.registerReceiver(receiver, filter)
    }

    override fun stop() {
        super.stop()
        try {
            context.unregisterReceiver(receiver)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }
}