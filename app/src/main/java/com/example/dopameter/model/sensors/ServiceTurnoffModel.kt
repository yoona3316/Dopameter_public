package com.example.dopameter.model.sensors

import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

data class ServiceTurnoffModel(
    override var time: Long?
): ModelInterface {

}

object ServiceTurnoffListAdapter:
    ModelAdapterInterface<ServiceTurnoffModel> {
    override var queue: Queue<ServiceTurnoffModel> = ConcurrentLinkedQueue()
    override var mModelInterface: Class<ServiceTurnoffModel> = ServiceTurnoffModel::class.java

}