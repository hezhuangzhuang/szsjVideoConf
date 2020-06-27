package com.hw.baselibrary.common

data class BaseData<T>(
    val dataList: ArrayList<T>,
    val data: ArrayList<T>,
    val msg: String,
    val result: String,
    val responseCode: Int
)