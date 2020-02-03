package com.hw.baselibrary.common

data class BaseData<T>(
    val dataList: ArrayList<T>,
    val data: ArrayList<T>,
    val message: String,
    val result: String,
    val responseCode: Int
)