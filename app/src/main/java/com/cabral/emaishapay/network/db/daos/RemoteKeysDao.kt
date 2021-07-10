/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cabral.emaishapay.network.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cabral.emaishapay.network.db.entities.RemoteKeys

@Dao
interface RemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<RemoteKeys>)

    @Query("SELECT * FROM remote_keys WHERE id = :orderId AND type='order' ")
    suspend fun remoteKeysOrderId(orderId: String): RemoteKeys?

    @Query("SELECT * FROM remote_keys WHERE id = :orderId AND type='product' ")
    suspend fun remoteKeysProductId(orderId: String): RemoteKeys?

    @Query("DELETE FROM remote_keys WHERE type='order'  ")
    suspend fun clearOrderRemoteKeys()

    @Query("DELETE FROM remote_keys WHERE type='product'  ")
    suspend fun clearProductRemoteKeys()
}
