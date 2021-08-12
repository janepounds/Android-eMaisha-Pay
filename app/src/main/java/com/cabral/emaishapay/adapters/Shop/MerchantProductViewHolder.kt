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

import android.app.AlertDialog
import android.content.Context
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.cabral.emaishapay.DailogFragments.shop.ShopProductPreviewDialog
import com.cabral.emaishapay.R
import com.cabral.emaishapay.activities.WalletHomeActivity
import com.cabral.emaishapay.databinding.ProductItemBinding
import com.cabral.emaishapay.modelviews.MerchantProductViewModel
import com.cabral.emaishapay.network.db.entities.EcProduct
import es.dmoral.toasty.Toasty

/**
 * View Holder for an Order RecyclerView list item.
 */
class MerchantProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {


    fun bind(product: EcProduct?, context: Context) {
        binding.productData = product
        val options = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.new_product)
                .error(R.drawable.new_product)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH)
        try {
            Glide.with(context).load(Base64.decode( product?.product_image ?: "", Base64.DEFAULT)).apply(options).into( binding.productImage )
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
        binding.root.setOnClickListener {

            if(product!=null){
                val ft: FragmentTransaction = fm.beginTransaction()
                val prev: Fragment? = fm.findFragmentByTag("dialog")
                if (prev != null) {
                    ft.remove(prev)
                }
                ft.addToBackStack(null)
                // Create and show the dialog.
                val productPreviewDialog: DialogFragment = ShopProductPreviewDialog(product, viewModel)
                productPreviewDialog.show(ft, "dialog")
            }

        }

        binding.imgDelete.setOnClickListener(View.OnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setMessage(R.string.want_to_delete_product)
                    .setCancelable(false)
                    .setPositiveButton(R.string.yes) { dialog, which ->
                        try {
                            val wallet_id = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, context)
                            viewModel.deleteProduct(product!!, wallet_id!!)
                            Toasty.error(context, R.string.product_deleted, Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(context, R.string.failed, Toast.LENGTH_SHORT).show()
                        }
                        dialog.cancel()
                    }
                    .setNegativeButton(R.string.no) { dialog, which -> // Perform Your Task Here--When No is pressed
                        dialog.cancel()
                    }.show()
        })

        binding.executePendingBindings()
    }

    companion object {
        private lateinit var binding: ProductItemBinding
        private lateinit var fm: FragmentManager
        private lateinit var viewModel: MerchantProductViewModel

        fun create(parent: ViewGroup, fm: FragmentManager, viewModel: MerchantProductViewModel): MerchantProductViewHolder {
            binding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.product_item, parent, false)
            Companion.fm = fm
            Companion.viewModel = viewModel

            return MerchantProductViewHolder(binding.root)
        }
    }
}
