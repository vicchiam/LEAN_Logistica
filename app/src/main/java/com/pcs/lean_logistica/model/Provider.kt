package com.pcs.lean_logistica.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class Provider(
    @SerializedName("PROVEEDOR") val code: Int,
    @SerializedName("NOMBRE") val name: String,
    @SerializedName("CANTIDAD") val amount: Float,
    @SerializedName("OCS") val purchaseOrders: String,
    @SerializedName("ARTICULOS") val articles: String,
    @SerializedName("DESCRIPCIONES") val descriptions: String,
    @SerializedName("CANTIDADES") val quantities: String,
    @SerializedName("UNDS") val unds: String,
    @SerializedName("ENTREGA") val dateDelivery: Date,
    @SerializedName("OBS") val obs: String
){

    fun getSearchCriteria(): String{
        return "$code $name".toLowerCase()
    }

}