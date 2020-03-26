package com.pcs.lean_logistica.model

import com.google.gson.annotations.SerializedName
import com.pcs.lean_logistica.tools.Utils
import java.util.*

data class Download(
    @SerializedName("position") var position: Int = 0,
    @SerializedName("id") var id: Long = 0,
    @SerializedName("provider") var provider: Int = 0,
    @SerializedName("name") var name: String = "",
    @SerializedName("operators") var operators: Int = 1,
    @SerializedName("type") var type: Int = 1,
    @SerializedName("start") var start: Date? = null,
    @SerializedName("end") var end: Date? = null,
    @SerializedName("pallets") var pallets: Int = 0
){

    val pending: Boolean
        get() = (start!=null && end==null)

    fun isValid(): Boolean{
        return (name.isNotEmpty() && operators>0 && pallets>0)
    }

    fun getSearchCriteria(): String{
        if(start==null)
            return ""
        return Utils.dateToString(start!!)
    }

}