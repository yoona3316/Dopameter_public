package com.example.dopameter.model.sensors

import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.collections.ArrayList

data class ScreenModel(
    override var time: Long?,
    val type: Int)
    : ModelInterface {
    companion object {
        const val SCREEN_ON = 0
        const val SCREEN_OFF = 1
    }
}

object ScreenListAdapter:
    ModelAdapterInterface<ScreenModel> {
    override var queue: Queue<ScreenModel> = ConcurrentLinkedQueue()
    override var mModelInterface: Class<ScreenModel> = ScreenModel::class.java

}