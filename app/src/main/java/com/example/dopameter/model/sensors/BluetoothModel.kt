package com.example.dopameter.model.sensors

import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.collections.ArrayList

data class BluetoothModel(
    override var time: Long?,
    val on: Boolean,
    val MAC: String?,
    val type: Int?,
    val class_id: Int?,
    val level: Int?,
    val name: String?
): ModelInterface {
    override fun equals(other: Any?): Boolean {
        // 30 seconds
        val timeLimit = 30000
        if (other !is BluetoothModel) return false
        val limit = other.time?.let { this.time?.minus(it) ?:40000 < timeLimit} ?: false
        return other.MAC == this.MAC && limit
    }
}

object BluetoothListAdapter:
    ModelAdapterInterface<BluetoothModel> {
    override var queue: Queue<BluetoothModel> = ConcurrentLinkedQueue()
    override var mModelInterface: Class<BluetoothModel> = BluetoothModel::class.java

    override fun add(data: BluetoothModel) {
        if (!queue.contains(data)) {
            super.add(data)
        }
    }

}