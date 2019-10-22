package com.pcs.lean_logistica.model

import com.pcs.lean_logistica.tools.Utils
import java.util.*

data class Download(var position: Int = 0, var id: Long = 0, var provider: Int = 0, var name: String = "", var operators: Int = 1, var type: String = "SI", var start: Date? = null, var end: Date? = null){

    val pending: Boolean
        get() = (start!=null && end==null)

    fun isValid(): Boolean{
        return (name.isNotEmpty() && operators>0)
    }

    fun getSearchCriteria(): String{
        if(start==null)
            return "";
        return Utils.dateToString(start!!)
    }

}