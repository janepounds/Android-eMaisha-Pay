package com.cabral.emaishapay.adapters

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.cabral.emaishapay.DailogFragments.WalletTransactionsReceiptDialog
import com.cabral.emaishapay.R
import com.cabral.emaishapay.activities.WalletHomeActivity
import com.cabral.emaishapay.databinding.WalletTransactionCardBinding
import com.cabral.emaishapay.models.WalletTransactionResponse
import com.cabral.emaishapay.network.db.entities.MerchantOrder
import com.cabral.emaishapay.network.db.entities.Transactions
import com.cabral.emaishapay.network.db.entities.UserTransactions
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class TransactionsViewHolder(v: View, fm:FragmentManager) : RecyclerView.ViewHolder(v) {


    private var transactionDetails: UserTransactions? = null

    init {
                v.setOnClickListener {
                    //Log.e("Reference Number", transaction.getReferenceNumber()+" is "+transaction.isPurchase());
                    fm?.let {
                        val walletTransactionsReceiptDialog = WalletTransactionsReceiptDialog(transactionDetails)

                        walletTransactionsReceiptDialog.show(fm, "walletTransactionsReceiptDialog")
                    }
            }
        }

        fun bind(transaction: UserTransactions?, context: Context) {
        val localFormat1 = SimpleDateFormat("MMM dd, yyyy", Locale.US)
        val localFormat2 = SimpleDateFormat("HH:mm a", Locale.ENGLISH)
        localFormat1.timeZone = TimeZone.getTimeZone("UTC+3")
        localFormat2.timeZone = TimeZone.getTimeZone("UTC+3")

        var currentDate: String = ""
        var currentTime: String = ""
        try {
            val incomingFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            incomingFormat.timeZone = TimeZone.getTimeZone("UTC+3")
            currentDate = localFormat1.format(incomingFormat.parse(transaction?.date))
            currentTime = localFormat2.format(incomingFormat.parse(transaction?.date))
//            if (position != 0) prevDate = localFormat1.format(incomingFormat.parse(dataList.get(position - 1).getDate()))
            binding.date.text = currentDate+" ,"+currentTime
            if (transaction != null) {
                binding.userName?.text = transaction.receiver

                if (!TextUtils.isEmpty(transaction.receiverUserId) && transaction.receiverUserId.equals(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, context), ignoreCase = true)) {
                    binding.amount.text = "+ UGX " + NumberFormat.getInstance().format(transaction.amount) + ""
                    binding.amount.setTextColor(Color.parseColor("#2E84BE"))
                    binding.initials.setBackgroundResource(R.drawable.rectangular_blue_solid)

                    if (transaction.sender.isEmpty()) {
                        binding.initials.text = getNameInitials(transaction.sender)
                        binding.userName.text = transaction.sender
                    } else {
                        binding.initials.text = getNameInitials(transaction.receiver)
                        binding.userName.text =transaction.receiver
                    }
                } else {

                    binding.amount.text = "- UGX " + NumberFormat.getInstance().format(0 - transaction.amount) + ""
                    binding.amount.setTextColor(Color.parseColor("#dc4436"))
                    binding.initials.setBackgroundResource(R.drawable.rectangular_red_solid)
                    binding.initials.text = getNameInitials(transaction.receiver)
                    binding.userName.text = transaction.receiver
                }

            }


        } catch (e: ParseException) {
            e.printStackTrace()
            Log.e("TransactionError", e.message!!)
        }
    }

    private fun getNameInitials(name: String): CharSequence? {
        if (name == null || name.isEmpty()) return ""
        var ini = "" + name[0]
        // we use ini to return the output
        // we use ini to return the output
        for (i in name.indices) {
            if (name[i] == ' ' && i + 1 < name.length && name[i + 1] != ' ' && ini.length != 2) {
                //if i+1==name.length() you will have an indexboundofexception
                //add the initials
                ini += name[i + 1]
            }
        }
        //after getting "ync" => return "YNC"
        //after getting "ync" => return "YNC"
        return ini.toUpperCase()
    }



    companion object {
        private lateinit var binding: WalletTransactionCardBinding
        private lateinit var fm :FragmentManager
        fun create(parent: ViewGroup): TransactionsViewHolder {
            binding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.wallet_transaction_card, parent, false)
            return TransactionsViewHolder(binding.root,fm)
        }
    }
}