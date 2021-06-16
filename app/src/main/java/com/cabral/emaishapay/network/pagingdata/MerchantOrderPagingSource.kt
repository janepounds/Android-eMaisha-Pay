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

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.cabral.emaishapay.activities.WalletHomeActivity
import com.cabral.emaishapay.models.order_model.OrderDetails
import com.cabral.emaishapay.network.api_helpers.BuyInputsAPIClient
import com.cabral.emaishapay.network.pagingdata.MerchantOrderRepository.Companion.NETWORK_PAGE_SIZE
import retrofit2.HttpException
import java.io.IOException

private const val STARTING_PAGE_INDEX = 1

class MerchantOrderPagingSource(private val wallet_id: Int) : PagingSource<Int, OrderDetails>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, OrderDetails> {
        val position = params.key ?: STARTING_PAGE_INDEX
        return try {
            val access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN
            val response = BuyInputsAPIClient.getInstance().getPagedOrders(access_token,wallet_id.toString(), 1, "UGX", position, params.loadSize)
            val repos = response.data
            val nextKey = if (repos.isEmpty()) {
                null
            } else {
                // initial load size = 3 * NETWORK_PAGE_SIZE
                // ensure we're not requesting duplicating items, at the 2nd request
                position + (params.loadSize / NETWORK_PAGE_SIZE)
            }
            LoadResult.Page(
                data = repos,
                prevKey = if (position == STARTING_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    // The refresh key is used for the initial load of the next PagingSource, after invalidation
    override fun getRefreshKey(state: PagingState<Int, OrderDetails>): Int? {
        // We need to get the previous key (or next key if previous is null) of the page
        // that was closest to the most recently accessed index.
        // Anchor position is the most recently accessed index
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
