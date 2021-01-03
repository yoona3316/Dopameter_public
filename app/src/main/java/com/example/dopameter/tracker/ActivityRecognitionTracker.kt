package com.example.dopameter.tracker

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.example.dopameter.BuildConfig
import com.example.dopameter.Utils
import com.example.dopameter.model.sensors.ActivityRecognitionListAdapter
import com.example.dopameter.model.sensors.ActivityRecognitionModel
import com.google.android.gms.location.*
import java.lang.IllegalArgumentException

class ActivityRecognitionTracker(val context: Context): Tracker() {
    override val TAG: String = ActivityRecognitionTracker::class.java.simpleName
    val TRANSITIONS_RECEIVER_ACTION = BuildConfig.APPLICATION_ID + "TRANSITIONS_RECEIVER_ACTION"
    private var mPendingIntent : PendingIntent? = null

    private val transitionsReceiver = TransitionsReceiver()
    var transitions: MutableList<ActivityTransition> = mutableListOf()
    var request: ActivityTransitionRequest

    init {
        val activity = listOf(
            DetectedActivity.IN_VEHICLE, DetectedActivity.ON_BICYCLE, DetectedActivity.ON_FOOT,
            DetectedActivity.RUNNING, DetectedActivity.STILL,
            DetectedActivity.WALKING)
        val transition = listOf(ActivityTransition.ACTIVITY_TRANSITION_ENTER, ActivityTransition.ACTIVITY_TRANSITION_EXIT)
        for (a in activity) {
            for (t in transition) {
                transitions.plusAssign(
                    ActivityTransition.Builder()
                        .setActivityType(a)
                        .setActivityTransition(t)
                        .build()
                )
            }
        }
        request = ActivityTransitionRequest(transitions)
    }

    override fun start() {
        super.start()
        var intent = Intent(TRANSITIONS_RECEIVER_ACTION)
        mPendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
        ActivityRecognition.getClient(context)
            .requestActivityTransitionUpdates(request, mPendingIntent)
            .addOnSuccessListener {
                Log.d(TAG, "start task")
            }
            .addOnFailureListener{
                Log.d(TAG, "fail task")
                it.printStackTrace()
            }

        context.registerReceiver(transitionsReceiver, IntentFilter(TRANSITIONS_RECEIVER_ACTION))

    }

    override fun stop() {
        super.stop()
        ActivityRecognition.getClient(context)
            .removeActivityTransitionUpdates(mPendingIntent)
            .addOnSuccessListener {
                mPendingIntent?.cancel()
            }
            .addOnFailureListener{e ->
                Log.e(TAG, e.message)
            }
        try {
            context.unregisterReceiver(transitionsReceiver)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }
}
class TransitionsReceiver : BroadcastReceiver() {
    val TAG = TransitionsReceiver::class.java.simpleName
    override fun onReceive(context: Context?, intent: Intent?) {

        if (ActivityTransitionResult.hasResult(intent)) {
            val result = ActivityTransitionResult.extractResult(intent)
            result?.let {
                for (event in it.transitionEvents) {
                    ActivityRecognitionListAdapter.add(
                        ActivityRecognitionModel(
                            Utils.getUnixNow(),
                            event.elapsedRealTimeNanos,
                            event.activityType,
                            event.transitionType
                        )
                    )
                }
            }
        }
    }
}