package com.example.dopameter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat.startForegroundService
import com.example.dopameter.model.sensors.BootListAdapter
import com.example.dopameter.model.sensors.BootModel

class BootBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        BootListAdapter.add(
            BootModel(
                Utils.getUnixNow(),
                true
            )
        )
        Intent(context, TrackerService::class.java).also {
            context?.startForegroundService(it)
        }
    }
}