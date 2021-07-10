package com.cabral.emaishapay.adapters.Shop

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.cabral.emaishapay.modelviews.MerchantProductViewModel
import com.cabral.emaishapay.network.db.entities.EcProduct

/**
 * Adapter for the list of Orders.
 */
class MerchantProductsAdapter(viewModel: MerchantProductViewModel, fm: FragmentManager, context: Context) : PagingDataAdapter<EcProduct, MerchantProductViewHolder>(ORDER_COMPARATOR) {
    private val fm: FragmentManager = fm
    private val viewModel: MerchantProductViewModel = viewModel
    private val context: Context = context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MerchantProductViewHolder {
        //Log.w("OncreateViewHolder"," OcreateViewHolder")
        return MerchantProductViewHolder.create(parent, fm, viewModel)
    }

    override fun onBindViewHolder(holder: MerchantProductViewHolder, position: Int) {
        val productItem = getItem(position)
        Log.w("orderItemW1",position.toString()+" ")
        if (productItem != null) {
            Log.w("orderItemW", productItem.product_id!!)

            holder.bind(productItem, context)
        }
    }

    companion object {
        private val ORDER_COMPARATOR = object : DiffUtil.ItemCallback<EcProduct>() {
            override fun areItemsTheSame(oldItem: EcProduct, newItem: EcProduct): Boolean =
                oldItem.product_id == newItem.product_id

            override fun areContentsTheSame(oldItem: EcProduct, newItem: EcProduct): Boolean =
                oldItem.product_id == newItem.product_id
        }
    }
}
