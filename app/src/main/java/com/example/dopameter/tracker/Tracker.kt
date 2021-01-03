package com.example.dopameter.tracker

import android.util.Log
import com.example.dopameter.Utils

open class Tracker: TrackerInterface {
    override val TAG: String
        get() = Tracker::class.java.simpleName

    override fun start() {
        Log.d(TAG, "Running at"+Utils.getUnixNow())
    }

    override fun stop() {
        Log.d(TAG, "Stopping at"+Utils.getUnixNow())
    }

}