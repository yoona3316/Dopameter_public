package com.example.dopameter.tracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.example.dopameter.ApiManager
import com.example.dopameter.TrackerService
import com.example.dopameter.Utils
import com.example.dopameter.model.sensors.BootListAdapter
import com.example.dopameter.model.sensors.BootModel
import java.lang.IllegalArgumentException

class ShutDownTracker(val context: Context?): Tracker() {
    override val TAG: String = ShutDownTracker::class.java.simpleName
    val filter = IntentFilter(Intent.ACTION_SHUTDOWN)
    val receiver = ShutDownBroadcastReceiver(context)

    override fun start() {
        super.start()
        context?.registerReceiver(receiver, filter)
    }

    override fun stop() {
        super.stop()
        try {
            context?.unregisterReceiver(receiver)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }
}
class ShutDownBroadcastReceiver(val context: Context?) : BroadcastReceiver() {
    val TAG = ShutDownBroadcastReceiver::class.java.simpleName
    override fun onReceive(context: Context?, intent: Intent?) {
        BootListAdapter.add(
            BootModel(
                Utils.getUnixNow(),
                false
            )
        )
        context?.run{
            try {
                val dateTimeKey = Utils.getDateTimeKey()
                for (listAdapter in TrackerService.TRACKERS) {
                    Log.d(TAG, "$TAG started, $listAdapter")
                    ApiManager.writeDataInJson(context, listAdapter, dateTimeKey)
                    ApiManager.uploadDataInRealtime(context, listAdapter)
                }
                for (listAdapter in TrackerService.ONCE_TRACKERS) {
                    if (listAdapter.queue.isEmpty()) continue
                    ApiManager.writeDataInJson(context, listAdapter, dateTimeKey)
                    ApiManager.uploadDataInRealtime(context, listAdapter)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}