package com.example.dopameter.model.sensors

import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.collections.ArrayList

// TODO(Ranking 관련 추가 데이터 조사 필요)
data class Ranking (
    val key: String,
    val rank: Int,
    val userSentiment: Int?
)

data class NotificationRankingModel(
    override var time: Long?
): ModelInterface {
    var rankingList: ArrayList<Ranking> = ArrayList()

    fun add(ranking: Ranking) {
        rankingList.add(ranking)
    }

}

object NotificationRankingListAdapter:
    ModelAdapterInterface<NotificationRankingModel> {
    override var queue: Queue<NotificationRankingModel> = ConcurrentLinkedQueue()
    override var mModelInterface: Class<NotificationRankingModel> = NotificationRankingModel::class.java

}