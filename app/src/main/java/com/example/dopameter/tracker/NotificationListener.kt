package com.example.dopameter.tracker

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.dopameter.Utils
import com.example.dopameter.model.sensors.NotificationListAdapter
import com.example.dopameter.model.sensors.NotificationModel

class NotificationListener: NotificationListenerService() {
    val TAG = this::class.java.simpleName
    companion object {
        var mActiveNotification: Array<StatusBarNotification>? = null
        var mCurrentOrderedKeys: Array<String>? = null
        var mCurrentRanking: RankingMap? = null
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d(TAG, "is running")
        mActiveNotification = this.activeNotifications

        mCurrentOrderedKeys = this.currentRanking.orderedKeys
        mCurrentRanking = this.currentRanking

        this.getActiveNotifications().forEach {
            NotificationListAdapter.add(
                NotificationModel(
                    NotificationModel.POST,
                    it.postTime,
                    it.key,
                    it.packageName,
                    null
                )
            )
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?, rankingMap: RankingMap?) {
        super.onNotificationPosted(sbn, rankingMap)
        mActiveNotification = this.activeNotifications

        mCurrentOrderedKeys = this.currentRanking.orderedKeys
        mCurrentRanking = this.currentRanking

        sbn?.let{
            NotificationListAdapter.add(
                NotificationModel(
                    NotificationModel.POST,
                    it.postTime,
                    it.key,
                    it.packageName,
                    null
                )
            )
        }

    }

//    => api26
    override fun onNotificationRemoved(
        sbn: StatusBarNotification?,
        rankingMap: RankingMap?,
        reason: Int
    ) {
        super.onNotificationRemoved(sbn, rankingMap, reason)
        mActiveNotification = this.activeNotifications

        mCurrentOrderedKeys = this.currentRanking.orderedKeys
        mCurrentRanking = this.currentRanking

        sbn?.let{
            NotificationListAdapter.add(
                NotificationModel(
                    NotificationModel.REMOVED,
                    Utils.getUnixNow(),
                    it.key,
                    it.packageName,
                    reason
                )
            )
        }
    }

}