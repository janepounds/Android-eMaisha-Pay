package com.cabral.emaishapay.adapters

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.cabral.emaishapay.network.db.entities.Transactions

class WalletTransactionsAdapter : PagingDataAdapter<Transactions, TransactionsViewHolder>(ORDER_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionsViewHolder {

        return TransactionsViewHolder.create(parent)
    }


    override fun onBindViewHolder(holder: TransactionsViewHolder, position: Int) {
        val transactionItem = getItem(position)

        if (transactionItem != null) {
            holder.bind(transactionItem)
        }
    }

    companion object {
        private val ORDER_COMPARATOR = object : DiffUtil.ItemCallback<Transactions>() {
            override fun areItemsTheSame(oldItem: Transactions, newItem: Transactions): Boolean =
                    oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Transactions, newItem: Transactions): Boolean =
                    oldItem.id == newItem.id
        }
    }
}