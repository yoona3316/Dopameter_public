package com.example.dopameter.model.sensors

import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.collections.ArrayList

data class BleModel(
    override var time: Long?,
    val timeStampNanos: Long?,
    val MAC: String,
    val class_id: Int,
    val level: Int,
    val name: String?
): ModelInterface {
    override fun equals(other: Any?): Boolean {
        if (other !is BleModel) return false
        val limit = other.time?.let { this.time?.minus(it) ?:100 < 60} ?: false
        return other.MAC == this.MAC && limit
    }
}

object BleListAdapter:
    ModelAdapterInterface<BleModel> {
    override var queue: Queue<BleModel> = ConcurrentLinkedQueue()
    override var mModelInterface: Class<BleModel> = BleModel::class.java

}