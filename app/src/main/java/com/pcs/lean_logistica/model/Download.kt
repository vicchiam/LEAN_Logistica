package com.pcs.lean_logistica.model

import java.util.*

data class Download(var id: Long = 0, var provider: Int = 0, var name: String = "", var operators: Int = 1, var type: String = "SI", var start: Date? = null, var end: Date? = null){

    fun isValid(): Boolean{
        return (name.isNotEmpty() && operators>0)
    }

}