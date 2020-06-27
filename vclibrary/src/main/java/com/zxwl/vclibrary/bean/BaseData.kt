package com.zxwl.vclibrary.bean


data class BaseData<T>(
    val dataList: List<T>,
    val data: List<T>,
    val msg: String,
    val result: String,
    val responseCode: Int
)