package com.example.dopameter.tracker

import android.annotation.SuppressLint
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.util.Log
import com.example.dopameter.ApiManager
import com.example.dopameter.Utils
import com.example.dopameter.model.sensors.UsageStatsListAdapter
import java.text.SimpleDateFormat
import java.util.*

//AppUsageTracker(this, UsageStatsManager.INTERVAL_MONTHLY, listOf(Utils.getUnixByMonth(-1), Utils.getUnixNow())).start()

//TODO(ActivityNum, RunningNum- task, process? / Activity Manager)
class AppUsageTracker(val context: Context, val days: Int): Tracker() {
    init {
        if (days<0) throw RuntimeException("days should be positive")
    }
    override val TAG: String
        get() = AppUsageTracker::class.java.simpleName

    private val usageStatsManager: UsageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    @SuppressLint("SimpleDateFormat")
    override fun start() {
        super.start()
        for (day in 0 until days) {
            val start = Utils.getUnixByDay(-day-1)
            val end = Utils.getUnixByDay(-day)
            val query = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end)
            UsageStatsListAdapter.add(Utils.getUnixNow(), query)

            val date = Date(end)
            var format = SimpleDateFormat("yyyy-MM-dd HH:mm")
            val dateTimeKey = format.format(date)
            ApiManager.writeDataInJson(context, UsageStatsListAdapter, dateTimeKey)
        }
    }

    override fun stop() {
        super.stop()
    }

}