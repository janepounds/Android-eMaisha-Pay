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

import androidx.paging.PagingSource
import androidx.room.*
import com.cabral.emaishapay.network.db.entities.MerchantOrder

@Dao
interface MerchantOrderDao {

    @Query("SELECT * FROM ShopOrder ORDER BY order_id DESC")
    fun getOrderList(): PagingSource< Int, MerchantOrder>

    @Query("SELECT ShopOrder.* FROM ShopOrder JOIN ShopOrderFts ON (ShopOrderFts.rowid=ShopOrder.id) WHERE ShopOrderFts MATCH :searchKey ORDER BY ShopOrder.order_id DESC")
    fun searchOders(searchKey : String): PagingSource< Int, MerchantOrder>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(orderList: List<MerchantOrder>)


    @Query("DELETE  FROM ShopOrder")
    suspend fun clearOrders()

}
