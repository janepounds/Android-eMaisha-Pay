package com.cabral.emaishapay.network.db.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.cabral.emaishapay.models.WalletTransactionResponse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.ArrayList

@Entity(tableName = "Transactions")
data class Transactions (
        @PrimaryKey(autoGenerate = true) @field:SerializedName("id") var id: Int=0,
        @field:SerializedName("cashin") var cashin: Double?=null,
        @field:SerializedName("cashout") var cashout: Double?=null,
        @field:SerializedName("bank") var bank: Double?=null,
        @field:SerializedName("mobileMoney") var mobileMoney: Double?=null,
        @Ignore @field:SerializedName("transactions")  var transactions: List<UserTransactions?>? = ArrayList()
) : Serializable
