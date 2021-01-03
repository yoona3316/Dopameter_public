package com.example.dopameter.model.sensors

import android.provider.CallLog
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.collections.ArrayList

data class CallModel(
    override var time: Long?,
    val phnNumber: String?,
    val type: String?,
    val date: String?,
    val duration: String?
    )
: ModelInterface {
    var dir: String? = null
    init {
        when (type?.toInt()) {
            CallLog.Calls.OUTGOING_TYPE -> dir = "OUTGOING"
            CallLog.Calls.INCOMING_TYPE -> dir = "INCOMING"
            CallLog.Calls.MISSED_TYPE -> dir = "MISSED"
            else -> null
        }
    }
    companion object {
        const val DEFAULT = -1
        const val INCOMING_ANSWERED = 0
        const val INCOMING_MISSED = 1
        const val OUTGOING = 2
    }

    override fun equals(other: Any?): Boolean {
        if (other !is CallModel) return false
        return this.date==other.date && this.phnNumber==other.phnNumber
    }

}

object CallListAdapter:
    ModelAdapterInterface<CallModel> {
    override var queue: Queue<CallModel> = ConcurrentLinkedQueue()
    override var mModelInterface: Class<CallModel> = CallModel::class.java
    override fun add(data: CallModel) {
        if (!queue.contains(data))
            super.add(data)
    }
}