package com.pcs.lean_logistica.model

import com.google.gson.annotations.SerializedName
import com.pcs.lean_logistica.tools.Utils
import java.util.*

data class Upload(
    @SerializedName("position") var position: Int = 0,
    @SerializedName("id") var id: Long = 0,
    @SerializedName("dock") var dock: Int = 0,
    @SerializedName("operators") var operators: Int = 1,
    @SerializedName("pallets") var pallets: Int = 0,
    @SerializedName("start") var start: Date? = null,
    @SerializedName("end") var end: Date? = null
){
    val pending: Boolean
        get() = (start!=null && end==null)

    fun isValidStart(): Boolean{
        return (dock>0 && operators>0)
    }

    fun isValidEnd(): Boolean{
        return (pallets>0)
    }

    fun getSearchCriteria(): String{
        if(start==null)
            return ""
        return Utils.dateToString(start!!)
    }

    fun dockInUse(list: List<Upload>): Boolean{
        list.forEach{
            if(it.dock==this.dock)
                return true
        }
        return false
    }
}