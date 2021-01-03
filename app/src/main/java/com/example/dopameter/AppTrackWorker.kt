package com.example.dopameter

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.dopameter.tracker.AppUsageTracker

class AppTrackWorker(val context: Context, workerParameters: WorkerParameters): Worker(context, workerParameters) {
    val TAG = AppTrackWorker::class.java.simpleName
    override fun doWork(): Result {
        AppUsageTracker(context, 2).start()
        return Result.success()
    }
}