package com.hw.kotlinmvpandroidxframe.net.bean

/**
 * 参会人的bean
 */
data class SiteStatusInfo(
    val loudspeakerStatus: Int,
    val microphoneStatus: Int,
    val siteName: String,
    val siteStatus: Int,
    val siteType: Int,
    val siteUri: String
)