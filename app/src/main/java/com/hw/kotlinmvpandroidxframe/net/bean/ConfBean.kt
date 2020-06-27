package com.hw.kotlinmvpandroidxframe.net.bean

/**
 * 会议详情的bean
 */
data class ConfBean(
    val accessCode: String,
    val beginTime: String,
    val confName: String,
    val confStatus: Int,
    val creatorName: Any,
    val creatorUri: Any,
    val endTime: String,
    val siteStatusInfoList: List<SiteStatusInfo>,
    val smcConfId: String
)