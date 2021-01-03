package com.example.dopameter.model.sensors

import java.lang.RuntimeException
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.collections.ArrayList

data class PowerModel(
    override var time: Long?,
    val charged: Charged?,
    val docked: Docked?
)
: ModelInterface {
    init {
        if (charged==null && docked==null)
            throw RuntimeException("Validation Failed")
    }
}
data class Charged(
    val plug: Int,
    val batteryStatus: Int,
    val level: Int,
    val scale: Int,
    val batteryPct: Float?
)
data class Docked(
    val isDocked: Boolean,
    val isCar: Boolean,
    val isDesk: Boolean
)
object PowerListAdapter :
    ModelAdapterInterface<PowerModel> {
    override var queue: Queue<PowerModel> = ConcurrentLinkedQueue()
    override var mModelInterface: Class<PowerModel> = PowerModel::class.java
}