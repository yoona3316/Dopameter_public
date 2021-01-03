package com.example.dopameter.model.sensors

import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.collections.ArrayList

data class InstalledAppModel(
    override var time: Long?,
    val pkgName: String?
): ModelInterface {

}
object InstalledAppListAdapter: ModelAdapterInterface<InstalledAppModel> {
    override var queue: Queue<InstalledAppModel> = ConcurrentLinkedQueue()
    override var mModelInterface: Class<InstalledAppModel> = InstalledAppModel::class.java

}