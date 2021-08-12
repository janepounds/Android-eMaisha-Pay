package com.cabral.emaishapay.adapters


import android.content.Context
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.cabral.emaishapay.network.db.entities.Transactions
import com.cabral.emaishapay.network.db.entities.UserTransactions

 class WalletTransactionsAdapter(val context: Context) : PagingDataAdapter<UserTransactions, TransactionsViewHolder>(ORDER_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionsViewHolder {

        return TransactionsViewHolder.create(parent)
    }


    override fun onBindViewHolder(holder: TransactionsViewHolder, position: Int) {
        val transactionItem = getItem(position)

        if (transactionItem != null) {
            context?.let { holder.bind(transactionItem, context = it) }
        }
    }

    companion object {
        private val ORDER_COMPARATOR = object : DiffUtil.ItemCallback<UserTransactions>() {
            override fun areItemsTheSame(oldItem: UserTransactions, newItem: UserTransactions): Boolean =
                    oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: UserTransactions, newItem: UserTransactions): Boolean =
                    oldItem.id == newItem.id
        }
    }
}