package com.example.dopameter.model.sensors

import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

data class ActivityRecognitionModel(
    override var time: Long?,
    val elapsedTimeNanos: Long,
    val activity: Int?,
    val transition: Int?
) : ModelInterface {

}

object ActivityRecognitionListAdapter:
    ModelAdapterInterface<ActivityRecognitionModel> {
    override var queue: Queue<ActivityRecognitionModel> = ConcurrentLinkedQueue()
    override var mModelInterface: Class<ActivityRecognitionModel> = ActivityRecognitionModel::class.java

}