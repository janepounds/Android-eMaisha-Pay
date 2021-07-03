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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cabral.emaishapay.R
import com.cabral.emaishapay.activities.MerchantShopActivity
import com.cabral.emaishapay.network.db.entities.MerchantOrder

/**
 * View Holder for an Order RecyclerView list item.
 */
class MerchantProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val txt_customer_name = view.findViewById<TextView?>(R.id.txt_order_customer_name)
    private val txt_customer_address = view.findViewById<TextView?>(R.id.txt_order_customer_address)
    private val txt_order_status = view.findViewById<TextView?>(R.id.txt_order_status)

    private var orderDetails: MerchantOrder? = null

    init {
        view.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("order_details", orderDetails)
            MerchantShopActivity.navController.navigate(R.id.action_shopOrdersFragment_to_onlineOrderDetailsFragment, bundle)
        }
    }

    fun bind(orderDetails: MerchantOrder?) {
        if (orderDetails == null) {
            val resources = itemView.resources
            if (txt_customer_name != null) {
                txt_customer_name.text = resources.getString(R.string.loading)
            }
            if (txt_customer_address != null) {
                txt_customer_address.text = resources.getString(R.string.unknown)
            }
            if (txt_order_status != null) {
                txt_order_status.text = resources.getString(R.string.unknown)
            }
        } else {
            showOrderData(orderDetails)
        }
    }

    private fun showOrderData(orderDetails: MerchantOrder) {
        this.orderDetails = orderDetails

        if (txt_customer_name != null) {
            txt_customer_name.text = orderDetails.customer_name.toString()
        }
        if (txt_customer_address != null) {
            txt_customer_address.text = orderDetails.customer_address.toString()
        }
        if (txt_order_status != null) {
            txt_order_status.text = orderDetails.order_status
        }
    }

    companion object {
        fun create(parent: ViewGroup): MerchantProductViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.online_order_item, parent, false)
            return MerchantProductViewHolder(view)
        }
    }
}
