package com.cabral.emaishapay.adapters.Shop

import android.util.Log
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.cabral.emaishapay.network.db.entities.MerchantOrder

/**
 * Adapter for the list of Orders.
 */
class  MerchantOrdersAdapter : PagingDataAdapter<MerchantOrder, MerchantOrderViewHolder>(ORDER_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MerchantOrderViewHolder {
        Log.w("OncreateViewHolder"," OcreateViewHolder")
        return MerchantOrderViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: MerchantOrderViewHolder, position: Int) {
        val orderItem = getItem(position)
        Log.w("orderItemW1",position.toString()+" ")
        if (orderItem != null) {
            Log.w("orderItemW", orderItem.order_id!!)
            holder.bind(orderItem)
        }
    }

    companion object {
        private val ORDER_COMPARATOR = object : DiffUtil.ItemCallback<MerchantOrder>() {
            override fun areItemsTheSame(oldItem: MerchantOrder, newItem: MerchantOrder): Boolean =
                oldItem.order_id == newItem.order_id

            override fun areContentsTheSame(oldItem: MerchantOrder, newItem: MerchantOrder): Boolean =
                oldItem.order_id == newItem.order_id
        }
    }
}
