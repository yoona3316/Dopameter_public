package com.example.dopameter.tracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.example.dopameter.Utils
import com.example.dopameter.model.sensors.PackageListAdapter
import com.example.dopameter.model.sensors.PackageModel
import java.lang.IllegalArgumentException

class PackageTracker(val context: Context): Tracker() {
    override val TAG: String = PackageTracker::class.java.simpleName
    private val filter = IntentFilter()
    private val packageReceiver = PackageReceiver(context)
    init {
        filter.addDataScheme("package")
        filter.addAction(Intent.ACTION_PACKAGE_ADDED)
        filter.addAction(Intent.ACTION_PACKAGE_FIRST_LAUNCH)
//        Action app uninstalled
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED)
        filter.addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED)
//        Action app replaced
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED)
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED)
    }
    override fun start() {
        super.start()
        context.registerReceiver(packageReceiver, filter)
    }

    override fun stop() {
        super.stop()
        try {
            context.unregisterReceiver(packageReceiver)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }
}
class PackageReceiver(context: Context): BroadcastReceiver() {
    val TAG = PackageTracker::class.java.simpleName
    override fun onReceive(context: Context?, intent: Intent?) {
        var type = PackageModel.DEFAULT
        when (intent?.action) {
            Intent.ACTION_PACKAGE_ADDED, Intent.ACTION_PACKAGE_FIRST_LAUNCH -> {
                type = PackageModel.INSTALL
            }
            Intent.ACTION_PACKAGE_REMOVED, Intent.ACTION_PACKAGE_FULLY_REMOVED -> {
                type = PackageModel.UNINSTALL
            }
            Intent.ACTION_PACKAGE_REPLACED, Intent.ACTION_PACKAGE_CHANGED -> {
                type = PackageModel.UPDATE
            }
        }
        PackageListAdapter.add(
            PackageModel(
                Utils.getUnixNow(),
                intent?.data?.schemeSpecificPart,
                type,
                intent?.action
            )
        )
    }
}
