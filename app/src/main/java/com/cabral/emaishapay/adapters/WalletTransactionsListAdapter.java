package com.cabral.emaishapay.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cabral.emaishapay.DailogFragments.WalletTransactionsReceiptDialog;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.models.WalletTransaction;
import com.cabral.emaishapay.models.WalletTransactionReceiptResponse;
import com.cabral.emaishapay.models.WalletTransactionResponse;
import com.cabral.emaishapay.network.db.entities.UserTransactions;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

public class WalletTransactionsListAdapter  extends RecyclerView.Adapter<com.cabral.emaishapay.adapters.WalletTransactionsListAdapter.MyViewHolder> {
     private final List<UserTransactions> dataList;

    private final FragmentManager fm;
    Context context;

    public  class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textDate, textAmount, textPaidTo, textReceivedFrom, initials;
        ConstraintLayout debitLayout, creditLayout;
        ConstraintLayout transactionCardLayout;

        public MyViewHolder(View v, FragmentManager fm) {
            super(v);
            textDate = v.findViewById(R.id.date);
            textAmount = v.findViewById(R.id.amount);

            textPaidTo = v.findViewById(R.id.user_name);
            textReceivedFrom = v.findViewById(R.id.user_name);
            debitLayout = v.findViewById(R.id.layout_amount);
            creditLayout = v.findViewById(R.id.layout_amount);
            initials = v.findViewById(R.id.initials);
            transactionCardLayout = v.findViewById(R.id.layout_transaction_list_card);
            transactionCardLayout.setOnClickListener(this);
      }

        @Override
        public void onClick(View v) {
            UserTransactions transaction = dataList.get(getBindingAdapterPosition());
            //Log.e("Reference Number", transaction.getReferenceNumber()+" is "+transaction.isPurchase());
            if (fm!=null ){
                WalletTransactionsReceiptDialog walletTransactionsReceiptDialog = new WalletTransactionsReceiptDialog(transaction);

                walletTransactionsReceiptDialog.show(fm, "walletTransactionsReceiptDialog");

            }
        }
    }

    public WalletTransactionsListAdapter(List<UserTransactions> dataList, FragmentManager supportFragmentManager) {
        this.dataList = dataList;
        fm=supportFragmentManager;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wallet_transaction_card,parent,false);
        context=parent.getContext();
        MyViewHolder holder = new MyViewHolder( view,fm);
        return holder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        UserTransactions data = dataList.get(position);

        SimpleDateFormat localFormat1 = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
        SimpleDateFormat localFormat2 = new SimpleDateFormat("HH:mm a", Locale.ENGLISH);
        localFormat1.setTimeZone(TimeZone.getTimeZone("UTC+3"));
        localFormat2.setTimeZone(TimeZone.getTimeZone("UTC+3"));

        String currentDate , currentTime , prevDate;
        try {
            SimpleDateFormat incomingFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            incomingFormat.setTimeZone(TimeZone.getTimeZone("UTC+3"));
            currentDate = localFormat1.format(incomingFormat.parse(data.getDate()));
            currentTime = localFormat2.format(incomingFormat.parse(data.getDate()));

            if(position!=0)
                prevDate = localFormat1.format(incomingFormat.parse(dataList.get(position-1).getDate()));

            holder.textDate.setText((currentDate) +", "+ (currentTime));


            holder.textReceivedFrom.setText(data.getReceiver());

             //Log.w("TransactionType",data.getType());
            if(  !TextUtils.isEmpty(data.getReceiverUserId()) && data.getReceiverUserId().equalsIgnoreCase( WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, context))) {
                holder.textAmount.setText("+ UGX "+ NumberFormat.getInstance().format(data.getAmount())+"");
                holder.textAmount.setTextColor(Color.parseColor("#2E84BE"));
                holder.initials.setBackgroundResource(R.drawable.rectangular_blue_solid);
                if(data.getSender()!=null && !data.getSender().isEmpty()){
                    holder.initials.setText(getNameInitials(data.getSender()));
                    holder.textPaidTo.setText(data.getSender());
                }
                else{
                    holder.initials.setText(getNameInitials(data.getReceiver()));
                    holder.textPaidTo.setText(data.getReceiver());
                }
            }
            else {
                holder.textAmount.setText("- UGX "+ NumberFormat.getInstance().format(0-data.getAmount())+"");
                holder.textAmount.setTextColor(Color.parseColor("#dc4436"));
                holder.initials.setBackgroundResource(R.drawable.rectangular_red_solid);
                holder.initials.setText(getNameInitials(data.getReceiver()));
                holder.textPaidTo.setText(data.getReceiver());
            }

        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("TransactionError",e.getMessage());
        }

    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    private String getNameInitials(String name){
        if(name==null || name.isEmpty())
            return "";
        String ini = ""+name.charAt(0);
        // we use ini to return the output
        for (int i=0; i<name.length(); i++){
            if ( name.charAt(i)==' ' && i+1 < name.length() && name.charAt(i+1)!=' ' && ini.length()!=2 ){
                //if i+1==name.length() you will have an indexboundofexception
                //add the initials
                ini+=name.charAt(i+1);
            }
        }
        //after getting "ync" => return "YNC"
        return ini.toUpperCase();
    }
}
