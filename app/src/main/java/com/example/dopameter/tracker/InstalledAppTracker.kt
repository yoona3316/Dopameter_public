package com.example.dopameter.tracker

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.example.dopameter.ApiManager
import com.example.dopameter.MainActivity
import com.example.dopameter.Utils
import com.example.dopameter.model.sensors.*
import java.io.*


class InstalledAppTracker(val context: Context): Tracker() {
    override val TAG: String = InstalledAppTracker::class.java.simpleName
    override fun start() {
        val pm: PackageManager = context.packageManager
        val packages =
            pm.getInstalledApplications(PackageManager.GET_META_DATA)

        for (packageInfo in packages) {
            InstalledAppListAdapter.add(
                InstalledAppModel(
                    Utils.getUnixNow(),
                    packageInfo.packageName
                )
            )
        }

    }
}