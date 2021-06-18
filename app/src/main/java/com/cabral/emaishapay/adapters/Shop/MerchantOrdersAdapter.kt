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

import android.util.Log
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.cabral.emaishapay.models.order_model.OrderDetails
import com.cabral.emaishapay.network.db.entities.ShopOrder

/**
 * Adapter for the list of Orders.
 */
class MerchantOrdersAdapter : PagingDataAdapter<ShopOrder, MerchantOrderViewHolder>(ORDER_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MerchantOrderViewHolder {
        Log.w("OncreateViewHolder"," OcreateViewHolder")
        return MerchantOrderViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: MerchantOrderViewHolder, position: Int) {
        val orderItem = getItem(position)
        Log.w("orderItemW1",position.toString()+" ")
        if (orderItem != null) {
            Log.w("orderItemW",orderItem.order_id)
            holder.bind(orderItem)
        }
    }

    companion object {
        private val ORDER_COMPARATOR = object : DiffUtil.ItemCallback<ShopOrder>() {
            override fun areItemsTheSame(oldItem: ShopOrder, newItem: ShopOrder): Boolean =
                oldItem.order_id == newItem.order_id

            override fun areContentsTheSame(oldItem: ShopOrder, newItem: ShopOrder): Boolean =
                oldItem.order_id == newItem.order_id
        }
    }
}
