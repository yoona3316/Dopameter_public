package com.example.dopameter.model.Lights

import com.example.dopameter.model.sensors.ModelAdapterInterface
import com.example.dopameter.model.sensors.ModelInterface
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.collections.ArrayList

data class LightModel(
    override var time: Long?,
    val elapsedTime: Long?,
    val sensorName: String,
    val accuracy: Int?,
    val value: Float
): ModelInterface {
    override fun equals(other: Any?): Boolean {
//        5 minutes
        val timeLimit = 300000
        if (other !is LightModel) return false
        val limit = other.time?.let { this.time?.minus(it) ?: 400000 < timeLimit} ?: false
        return limit
    }

}

object LightListAdapter:
    ModelAdapterInterface<LightModel> {
    override var queue: Queue<LightModel> = ConcurrentLinkedQueue()
    override var mModelInterface: Class<LightModel> = LightModel::class.java
    override fun add(data: LightModel) {
        val test = queue
        if (!queue.contains(data)) {
            super.add(data)
        }
    }

}