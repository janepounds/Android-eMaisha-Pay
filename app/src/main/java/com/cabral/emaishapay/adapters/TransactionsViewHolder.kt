package com.cabral.emaishapay.adapters

import android.graphics.Color
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cabral.emaishapay.DailogFragments.WalletTransactionsReceiptDialog
import com.cabral.emaishapay.R
import com.cabral.emaishapay.activities.WalletHomeActivity
import com.cabral.emaishapay.adapters.Shop.MerchantOrderViewHolder
import com.cabral.emaishapay.models.WalletTransactionResponse
import com.cabral.emaishapay.network.db.entities.Transactions
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class TransactionsViewHolder(v: View) : RecyclerView.ViewHolder(v) {

    private val textDate = v.findViewById<TextView?>(R.id.date)
    private val textAmount = v.findViewById<TextView?>(R.id.amount)
    private val textPaidTo = v.findViewById<TextView?>(R.id.user_name)
    private val textReceivedFrom = v.findViewById<TextView?>(R.id.user_name)
    private val debitLayout = v.findViewById<LinearLayout?>(R.id.layout_amount)
    private val creditLayout = v.findViewById<LinearLayout?>(R.id.layout_amount)
    private val initials = v.findViewById<TextView?>(R.id.initials)
    private val transactionCardLayout = v.findViewById<LinearLayout?>(R.id.layout_transaction_list_card)
//    private val transactionCardLayout.setOnClickListener(this)

    private var transactionDetails: Transactions? = null

    init {
        v.setOnClickListener {
            val transaction: WalletTransactionResponse.TransactionData.Transactions = dataList.get(bindingAdapterPosition)
            //Log.e("Reference Number", transaction.getReferenceNumber()+" is "+transaction.isPurchase());
            if (fm != null) {
                val walletTransactionsReceiptDialog = WalletTransactionsReceiptDialog(transaction)

                walletTransactionsReceiptDialog.show(fm, "walletTransactionsReceiptDialog")
        }
    }

        fun getNameInitials(name: String?): Any {
            if (name == null || name.isEmpty()) return ""
            var ini = "" + name.get(0)
            // we use ini to return the output
            // we use ini to return the output
            for (i in 0 until name.length) {
                if (name.get(i) == ' ' && i + 1 < name.length && name.get(i + 1) != ' ' && ini.length != 2) {
                    //if i+1==name.length() you will have an indexboundofexception
                    //add the initials
                    ini += name.get(i + 1)
                }
            }
            //after getting "ync" => return "YNC"
            //after getting "ync" => return "YNC"
            return ini.toUpperCase()
        }
        }



        fun bind(transactionDetails: Transactions?) {
        val data: WalletTransactionResponse.TransactionData.Transactions = dataList.get(position)

        val localFormat1 = SimpleDateFormat("MMM dd, yyyy", Locale.US)
        val localFormat2 = SimpleDateFormat("HH:mm a", Locale.ENGLISH)
        localFormat1.timeZone = TimeZone.getTimeZone("UTC+3")
        localFormat2.timeZone = TimeZone.getTimeZone("UTC+3")

        val currentDate: String
        val currentTime: String
        val prevDate: String
        try {
            val incomingFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            incomingFormat.timeZone = TimeZone.getTimeZone("UTC+3")
            currentDate = localFormat1.format(incomingFormat.parse(data.date))
            currentTime = localFormat2.format(incomingFormat.parse(data.date))
            if (position != 0) prevDate = localFormat1.format(incomingFormat.parse(dataList.get(position - 1).getDate()))
//            textDate!!.text((currentDate) +", "+ (currentTime))
            textReceivedFrom?.text = data.receiver

            //Log.w("TransactionType",data.getType());
            if (!TextUtils.isEmpty(data.receiverUserId) && data.receiverUserId.equals(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, context), ignoreCase = true)) {
               textAmount.setText("+ UGX " + NumberFormat.getInstance().format(data.amount) + "")
                textAmount.setTextColor(Color.parseColor("#2E84BE"))
                initials.setBackgroundResource(R.drawable.rectangular_blue_solid)
                if (data.sender != null && !data.sender.isEmpty()) {
                    initials.setText(getNameInitials(data.sender))
                    textPaidTo.setText(data.sender)
                } else {
                    holder.initials.setText(getNameInitials(data.receiver))
                    holder.textPaidTo.setText(data.receiver)
                }
            } else {
                holder.textAmount.setText("- UGX " + NumberFormat.getInstance().format(0 - data.amount) + "")
                holder.textAmount.setTextColor(Color.parseColor("#dc4436"))
                holder.initials.setBackgroundResource(R.drawable.rectangular_red_solid)
                holder.initials.setText(getNameInitials(data.receiver))
                holder.textPaidTo.setText(data.receiver)
            }
        } catch (e: ParseException) {
            e.printStackTrace()
            Log.e("TransactionError", e.message!!)
        }
    }



    companion object {
        fun create(parent: ViewGroup): TransactionsViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.wallet_transaction_card, parent, false)
            return TransactionsViewHolder(view)
        }
    }
}