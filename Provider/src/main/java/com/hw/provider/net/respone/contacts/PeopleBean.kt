package com.hw.provider.net.respone.contacts

import com.chad.library.adapter.base.entity.MultiItemEntity
import java.io.Serializable

/**
 *author：pc-20171125
 *data:2020/1/8 16:10
 */
data class PeopleBean(
    var id: String,
    var sip: String,
    var sipAccount: String,
    var name: String,
    var firstLetter: String,
    var isCheck: Boolean = false,
    //0:在线，1:离线
    var online: Int = 1
) : Serializable, MultiItemEntity {

    override fun getItemType(): Int {
        //2代表返回的人员
        return 2
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PeopleBean

        if (id != other.id) return false
        if (sip != other.sip) return false
        if (sipAccount != other.sipAccount) return false
        if (name != other.name) return false
        if (firstLetter != other.firstLetter) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + sip.hashCode()
        result = 31 * result + sipAccount.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + firstLetter.hashCode()
        return result
    }


}