package com.example.dopameter.model.sensors

import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.collections.ArrayList

data class NotificationModel(
    val type: Int,
    override var time: Long?,
//    unique instance key for sbn
    val key: String,
    val pkgName: String,
    val cancelReason: Int?
): ModelInterface {
    companion object {
        const val DEFAULT = 0
        const val POST = 1
        const val REMOVED = 2
    }
}

object NotificationListAdapter:
    ModelAdapterInterface<NotificationModel> {
    override var queue: Queue<NotificationModel> = ConcurrentLinkedQueue()
    override var mModelInterface: Class<NotificationModel> = NotificationModel::class.java

}