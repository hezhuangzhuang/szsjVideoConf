package com.hw.baselibrary.utils

import android.content.Context
import android.content.SharedPreferences
import com.hw.baselibrary.common.BaseApp
import com.hw.baselibrary.common.BaseConstant

/**
 *author：pc-20171125
 *data:2019/11/7 15:39
 */
object AppPrefsUtils {
    private var sp: SharedPreferences =
        BaseApp.context.getSharedPreferences(BaseConstant.TABLE_PREFS, Context.MODE_PRIVATE)

    private var ed: SharedPreferences.Editor

    init {
        ed = sp.edit()
    }

    /**
     * boolean数据
     */
    fun putBoolean(key: String, value: Boolean) {
        ed.putBoolean(key, value)
        ed.commit()
    }

    /**
     * 获取boolean
     */
    fun getBoolean(key: String): Boolean {
        return sp.getBoolean(key, false)
    }

    /**
     * 存String数据
     */
    fun putString(key:String,value:String){
        ed.putString(key,value)
        ed.commit()
    }

    /**
     * 获取string
     */
    fun getString(key:String):String{
        return sp.getString(key,"")
    }

    /**
     * 存int数据
     */
    fun putInt(key:String,value: Int){
        ed.putInt(key,value)
        ed.commit()
    }

    /**
     * 获取int
     */
    fun getInt(key: String):Int{
        return sp.getInt(key,0)
    }

    /*
        Long数据
     */
    fun putLong(key: String, value: Long) {
        ed.putLong(key, value)
        ed.commit()
    }

    /*
        默认 0
     */
    fun getLong(key: String): Long {
        return sp.getLong(key, 0)
    }

    /*
        Set数据
     */
    fun putStringSet(key: String, set: Set<String>) {
        val localSet = getStringSet(key).toMutableSet()
        localSet.addAll(set)
        ed.putStringSet(key, localSet)
        ed.commit()
    }

    /*
        默认空set
     */
    fun getStringSet(key: String): Set<String> {
        val set = setOf<String>()
        return sp.getStringSet(key, set)
    }

    /*
        删除key数据
     */
    fun remove(key: String) {
        ed.remove(key)
        ed.commit()
    }

}