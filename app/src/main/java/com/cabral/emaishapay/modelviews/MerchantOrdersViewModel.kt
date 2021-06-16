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

package com.cabral.emaishapay.modelviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.cabral.emaishapay.models.order_model.OrderDetails
import com.cabral.emaishapay.network.pagingdata.MerchantOrderRepository
import kotlinx.coroutines.flow.Flow

class MerchantOrdersViewModel(private val repository: MerchantOrderRepository) : ViewModel() {
    private var currentQueryValue: String? = null

    private var currentSearchResult: Flow<PagingData<OrderDetails>>? = null

    fun searchOrders(queryString: String): Flow<PagingData<OrderDetails>> {
        val lastResult = currentSearchResult
        if (queryString == currentQueryValue && lastResult != null) {
            return lastResult
        }
        currentQueryValue = queryString
        val newResult: Flow<PagingData<OrderDetails>> = repository.getMerchantOrdersResultStream()
            .cachedIn(viewModelScope)
        currentSearchResult = newResult
        return newResult
    }

    class ViewModelFactory(private val repository: MerchantOrderRepository) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MerchantOrdersViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MerchantOrdersViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }


}
