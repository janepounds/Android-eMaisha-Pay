package com.cabral.emaishapay.network.db.entities

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "EcProduct", indices = arrayOf(Index(value = ["product_name"], unique = true)) )
data class EcProduct(
        @PrimaryKey @NonNull @field:SerializedName("id") val id: String,
        @field:SerializedName("product_id") val product_id: String?=null,
        @field:SerializedName("product_name") val product_name: String? = null,
        @field:SerializedName("product_code") val product_code: String? = null,
        @field:SerializedName("product_category") val product_category: String? = null,
        @field:SerializedName("product_description") val product_description: String? = null,
        @field:SerializedName("product_buy_price") val product_buy_price: String? = null,
        @field:SerializedName("product_sell_price") val product_sell_price: String? = null,
        @field:SerializedName("product_supplier") val product_supplier: String? = null,
        @field:SerializedName("product_image") val product_image: String? = null,
        @field:SerializedName("product_stock") val product_stock: String? = null,
        @field:SerializedName("product_weight_unit") val product_weight_unit: String? = null,
        @field:SerializedName("product_weight") val product_weight: String? = null,
        @field:SerializedName("manufacturer") val manufacturer: String? = null,
        @ColumnInfo(name = "sync_status", defaultValue = "0") var sync_status: String? = null
) : Serializable