/*
 * Copyright (C) 2018 The Android Open Source Project
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

package com.cabral.emaishapay.network.pagingdata

import androidx.paging.*
import com.cabral.emaishapay.network.api_helpers.EmaishaShopAPIService
import com.cabral.emaishapay.network.db.EmaishapayDb
import com.cabral.emaishapay.network.db.entities.EcProduct
import com.cabral.emaishapay.network.db.entities.MerchantOrder
import kotlinx.coroutines.flow.Flow

/**
 * Repository class that works with local and remote data sources.
 */
class MerchantRepository(private val wallet_id: Int, private val database: EmaishapayDb) {

    /**
     * fetch merchant orders, exposed as a stream of data that will emit
     * every time we get more data from the network.
     */

    fun getMerchantOrdersResultStream(query: String): Flow<PagingData<MerchantOrder>> {

        // appending '%' so we can allow other characters to be before and after the query string
        ///val dbQuery = "%${query.replace(' ', '%')}%"
        val pagingSourceFactory = { if(query.isEmpty() || query==null) database.merchantOrderDao()?.getOrderList() else database.merchantOrderDao()?.searchOders(query) }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
                config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
                remoteMediator = MerchantOrderRemoteMediator(
                        wallet_id,
                        EmaishaShopAPIService.create(),
                        database
                ),
                pagingSourceFactory = pagingSourceFactory as () -> PagingSource<Int, MerchantOrder>
        ).flow
    }
    fun getMerchantProductsResultStream(query: String): Flow<PagingData<EcProduct>> {

        // appending '%' so we can allow other characters to be before and after the query string
        ///val dbQuery = "%${query.replace(' ', '%')}%"
        val pagingSourceFactory = { if(query.isEmpty() || query==null) database.merchantProductDao()?.getProducts() else database.merchantProductDao()?.searchProducts(query) }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
                config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
                remoteMediator = MerchantProductRemoteMediator(
                        wallet_id,
                        EmaishaShopAPIService.create(),
                        database
                ),
                pagingSourceFactory = pagingSourceFactory as () -> PagingSource<Int, EcProduct>
        ).flow
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 20
    }
}
