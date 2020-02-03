package com.hw.provider.net.respone.user

data class LoginBean(
    val data: Data,
    val message: String,
    val responseCode: Int
){
    data class Data(
        val accountName: String,
        val depId: Int,
        val depName: String,
        val deviceId: Any,
        val firstLetter: Any,
        val fullAccount: String,
        val id: Int,
        val name: String,
        val password: String,
        val scIp: String,
        val scPort: String,
        val seesionId: Any,
        val selfPwd: Any,
        val sipAccount: String,
        val sipPassword: String,
        val sipState: Int,
        val sipStateVal: Any,
        val telephone: Any,
        val updateTime: String
    )
}