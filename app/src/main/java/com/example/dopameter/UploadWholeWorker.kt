package com.example.dopameter

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class UploadWholeWorker(val context: Context, workerParameters: WorkerParameters): Worker(context, workerParameters) {
    val TAG = UploadWholeWorker::class.java.simpleName
    override fun doWork(): Result {
        try {
            for (listAdapter in TrackerService.TRACKERS) {
                Log.d(TAG, "$TAG started, $listAdapter")
                ApiManager.uploadDataInRealtime(context, listAdapter)
            }
            for (listAdapter in TrackerService.ONCE_TRACKERS) {
                Log.d(TAG, "$TAG started, $listAdapter")
                ApiManager.uploadDataInRealtime(context, listAdapter)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure()
        }
        return Result.success()
    }

}
