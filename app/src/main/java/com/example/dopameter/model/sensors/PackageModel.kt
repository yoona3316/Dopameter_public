package com.example.dopameter.model.sensors


import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.collections.ArrayList

data class PackageModel(
    override var time: Long?,
    val pkgName: String?,
    val type: Int,
    val action: String?
): ModelInterface {
    companion object {
        const val DEFAULT = 0
        const val INSTALL = 1
        const val UNINSTALL = 2
        const val UPDATE = 3

    }
}
object PackageListAdapter: ModelAdapterInterface<PackageModel> {
    override var queue: Queue<PackageModel> = ConcurrentLinkedQueue()
    override var mModelInterface: Class<PackageModel> = PackageModel::class.java

}