package com.example.dopameter.model.Proximity

import com.example.dopameter.model.sensors.ModelAdapterInterface
import com.example.dopameter.model.sensors.ModelInterface
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.collections.ArrayList

data class ProximityModel(
    override var time: Long?,
    val elapsedTime: Long?,
    val sensorName: String,
    val accuracy: Int?,
    val value: Float
): ModelInterface {

}

object ProximityListAdapter:
    ModelAdapterInterface<ProximityModel> {
    override var queue: Queue<ProximityModel> = ConcurrentLinkedQueue()
    override var mModelInterface: Class<ProximityModel> = ProximityModel::class.java

}