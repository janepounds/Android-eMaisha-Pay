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

package com.cabral.emaishapay.adapters.Shop

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.cabral.emaishapay.models.order_model.OrderDetails

/**
 * Adapter for the list of Orders.
 */
class MerchantOrdersAdapter : PagingDataAdapter<OrderDetails, MerchantOrderViewHolder>(REPO_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MerchantOrderViewHolder {
        return MerchantOrderViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: MerchantOrderViewHolder, position: Int) {
        val repoItem = getItem(position)
        if (repoItem != null) {
            holder.bind(repoItem)
        }
    }

    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<OrderDetails>() {
            override fun areItemsTheSame(oldItem: OrderDetails, newItem: OrderDetails): Boolean =
                oldItem.ordersId == newItem.ordersId

            override fun areContentsTheSame(oldItem: OrderDetails, newItem: OrderDetails): Boolean =
                oldItem.ordersId == newItem.ordersId
        }
    }
}
