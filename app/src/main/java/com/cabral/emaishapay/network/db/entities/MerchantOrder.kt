package com.cabral.emaishapay.network.db.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

/**
 * Immutable model class for a Github repo that holds all the information about a repository.
 * Objects of this type are received from the Github API, therefore all the fields are annotated
 * with the serialized name.
 * This class also defines the Room repos table, where the repo [id] is the primary key.
 */
@Entity(tableName = "ShopOrder", indices = arrayOf(Index(value = ["order_id"], unique = true)) )
data class MerchantOrder(
        @PrimaryKey(autoGenerate = true) @field:SerializedName("id") var id: Int=0,
        @field:SerializedName("order_id") var order_id: String?=null,
        @field:SerializedName("order_date") var order_date: String?=null,
        @field:SerializedName("order_time") var order_time: String?=null,
        @field:SerializedName("order_type") var order_type: String?=null,
        @field:SerializedName("order_payment_method") var order_payment_method: String?=null,
        @field:SerializedName("customer_name") var customer_name: String?=null,
        @field:SerializedName("storage_status") var storage_status: String?=null,
        @field:SerializedName("discount") var discount: Double=0.0,
        @field:SerializedName("order_status") var order_status: String?=null,
        @field:SerializedName("customer_address") var customer_address: String?=null,
        @field:SerializedName("customer_cell") var customer_cell: String?=null,
        @field:SerializedName("delivery_fee") var delivery_fee: String?=null,
        @field:SerializedName("customer_email") var customer_email: String?=null,
        @Ignore @field:SerializedName("products")  var products: List<ShopOrderProducts?>? = ArrayList()
) : Serializable