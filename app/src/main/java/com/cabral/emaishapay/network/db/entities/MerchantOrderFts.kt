package com.cabral.emaishapay.network.db.entities

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "ShopOrderFts")
@Fts4(contentEntity = MerchantOrder::class)
data class MerchantOrderFts(
        @PrimaryKey @NonNull @field:SerializedName("rowid") var rowid: Int,
        @field:SerializedName("order_date") val order_date: String? = null,
        @field:SerializedName("order_type") val order_type: String? = null,
        @field:SerializedName("order_payment_method") val order_payment_method: String? = null,
        @field:SerializedName("order_status") val order_status: String? = null,
        @field:SerializedName("customer_name") val customer_name: String? = null,
        @field:SerializedName("customer_address") val customer_address: String? = null,
        @field:SerializedName("customer_cell") val customer_cell: String? = null,
        @field:SerializedName("customer_email") val customer_email: String? = null
)