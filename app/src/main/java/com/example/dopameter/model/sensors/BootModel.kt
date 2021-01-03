package com.example.dopameter.model.sensors

import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

data class BootModel(
    override var time: Long?,
    val on: Boolean
): ModelInterface {

}

object BootListAdapter :
    ModelAdapterInterface<BootModel> {
    override var queue: Queue<BootModel> = ConcurrentLinkedQueue()
    override var mModelInterface = BootModel::class.java

}