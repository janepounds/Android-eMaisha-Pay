package com.cabral.emaishapay.network.db.entities

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "TransactionsFts")
@Fts4(contentEntity = Transactions::class)
data class TransactionsFts (
        @PrimaryKey  var rowid: Int,
        @field:SerializedName("cashin") var cashin: Double?=null,
        @field:SerializedName("cashout") var cashout: Double?=null,
        @field:SerializedName("bank") var bank: Double?=null,
        @field:SerializedName("mobileMoney") var mobileMoney: Double?=null
        )
