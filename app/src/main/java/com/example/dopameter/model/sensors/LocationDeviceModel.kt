package com.example.dopameter.model.sensors

import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

data class LocationDeviceModel(
//    TODO(more fields to be added)
    override var time: Long?,
    val on: Boolean,
    val latitude: Double?,
    val longitude: Double?,
    val altitude: Double?
): ModelInterface {

}

object LocationListAdapter :
    ModelAdapterInterface<LocationDeviceModel> {
    override var queue: Queue<LocationDeviceModel> = ConcurrentLinkedQueue()
    override var mModelInterface = LocationDeviceModel::class.java

}