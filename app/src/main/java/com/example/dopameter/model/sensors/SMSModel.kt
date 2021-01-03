package com.example.dopameter.model.sensors

import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

data class SMSModel(
    override var time: Long?,
    val address: String?,
    val date: String?,
    val read: Int?,
    val protocol: String?,
    val body_length: Int?
): ModelInterface {

}

object SMSListAdapter:
    ModelAdapterInterface<SMSModel> {
    override var queue: Queue<SMSModel> = ConcurrentLinkedQueue()
    override var mModelInterface: Class<SMSModel> = SMSModel::class.java

}