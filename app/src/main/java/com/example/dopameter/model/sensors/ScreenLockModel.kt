package com.example.dopameter.model.sensors

import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.collections.ArrayList

data class ScreenLockModel(
    override var time: Long?,
    val type: Int
): ModelInterface {
    companion object {
        const val SCREEN_LOCK = 0
        const val SCREEN_UNLOCK = 1
    }
}
object ScreenLockListAdapter:
    ModelAdapterInterface<ScreenLockModel> {
    override var queue: Queue<ScreenLockModel> = ConcurrentLinkedQueue()
    override var mModelInterface: Class<ScreenLockModel> = ScreenLockModel::class.java

}