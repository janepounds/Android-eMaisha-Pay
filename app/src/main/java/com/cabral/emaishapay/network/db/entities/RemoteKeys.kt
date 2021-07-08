package com.cabral.emaishapay.network.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeys(
        @PrimaryKey val id: Int,
        val prevKey: Int?,
        val nextKey: Int?,
        @ColumnInfo(name = "type", defaultValue = "order") val type: String = "order"
)