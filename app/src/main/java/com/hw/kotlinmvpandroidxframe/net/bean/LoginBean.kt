package com.hw.kotlinmvpandroidxframe.net.bean

data class LoginBean(
    val account: Account,
    val id: String,
    val message: String,
    val result: String,
    val sessionID: String){
    data class Account(
        val account: String,
        val checkAdmin: String,
        val checkAdminVal: String,
        val departmentEmpId: String,
        val description: String,
        val deviceID: String,
        val firstChar: String,
        val flag: String,
        val id: String,
        val level: String,
        val levelName: String,
        val name: String,
        val partyBranchId: String,
        val password: String,
        val password1: String,
        val sessionID: String,
        val sex: String,
        val state: String,
        val styleInfo: String,
        val styleInfoPc: String,
        val telephone: String,
        val unitId: String,
        val url: String,
        val votes: String,
        val votesPercent: String
    )
}