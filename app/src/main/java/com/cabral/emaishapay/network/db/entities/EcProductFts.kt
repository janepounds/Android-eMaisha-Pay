package com.cabral.emaishapay.network.db.entities

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "EcProductFts" )
@Fts4(contentEntity = EcProduct::class)
data class EcProductFts(
        @PrimaryKey @NonNull @field:SerializedName("rowid") var rowid: Int,
        @field:SerializedName("product_name") val product_name: String? = null,
        @field:SerializedName("product_category") val product_category: String? = null,
        @field:SerializedName("product_description") val product_description: String? = null,
        @field:SerializedName("product_code") val product_code: String? = null,
        @field:SerializedName("product_supplier") val product_supplier: String? = null,
        @field:SerializedName("product_weight_unit") val product_weight_unit: String? = null,
        @field:SerializedName("product_weight") val product_weight: String? = null,
        @field:SerializedName("manufacturer") val manufacturer: String? = null
)