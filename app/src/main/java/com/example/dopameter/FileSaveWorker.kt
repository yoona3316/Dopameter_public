package com.example.dopameter

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.lang.Exception

class FileSaveWorker(val context: Context, workerParameters: WorkerParameters): Worker(context, workerParameters) {
    val TAG = FileSaveWorker::class.java.simpleName
    override fun doWork(): Result {
        try {
            val dateTimeKey = Utils.getDateTimeKey()
            for (listAdapter in TrackerService.TRACKERS) {
                Log.d(TAG, "$TAG started, $listAdapter")
                ApiManager.writeDataInJson(context, listAdapter, dateTimeKey)
            }
            for (listAdapter in TrackerService.ONCE_TRACKERS) {
                if (listAdapter.queue.isEmpty()) continue
                ApiManager.writeDataInJson(context, listAdapter, dateTimeKey)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure()
        }
        return Result.success()
    }
}