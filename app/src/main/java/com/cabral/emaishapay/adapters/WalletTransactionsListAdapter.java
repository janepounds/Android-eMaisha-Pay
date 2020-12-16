package com.cabral.emaishapay.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
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

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

public class WalletTransactionsListAdapter  extends RecyclerView.Adapter<com.cabral.emaishapay.adapters.WalletTransactionsListAdapter.MyViewHolder> {
     private List<WalletTransactionResponse.TransactionData.Transactions> dataList;

    private FragmentManager fm;
    Context context;

    public  class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textDate, textAmount, textPaidTo,textPaidTo_label, textReceivedFrom, initials;
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
            WalletTransactionResponse.TransactionData.Transactions transaction = dataList.get(getAdapterPosition());
            //Log.e("Reference Number", transaction.getReferenceNumber()+" is "+transaction.isPurchase());
            if (fm!=null ){
                Intent intent = new Intent(v.getContext(), WalletTransactionsReceiptDialog.class);
                //intent.putExtra("referenceNumber",transaction.getReferenceNumber());
                WalletTransactionsReceiptDialog walletTransactionsReceiptDialog = new WalletTransactionsReceiptDialog(transaction);

                walletTransactionsReceiptDialog.show(fm, "walletTransactionsReceiptDialog");

            }
        }
    }

    public WalletTransactionsListAdapter(List<WalletTransactionResponse.TransactionData.Transactions> dataList, FragmentManager supportFragmentManager) {
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

        WalletTransactionResponse.TransactionData.Transactions data = dataList.get(position);
        // Generate random ARGB colors
        Random rnd = new Random();
        int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

        // Set the generated color as the background for name initials
        holder.initials.getBackground().setColorFilter(currentColor, PorterDuff.Mode.SRC_OVER);

        SimpleDateFormat localFormat1 = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
        SimpleDateFormat localFormat2 = new SimpleDateFormat("HH:mm a", Locale.ENGLISH);
        localFormat1.setTimeZone(TimeZone.getDefault());
        localFormat2.setTimeZone(TimeZone.getDefault());

        String currentDate = null, currentTime = null, prevDate=null;
        try {
            SimpleDateFormat incomingFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            incomingFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            currentDate = localFormat1.format(incomingFormat.parse(data.getDate()));
            currentTime = localFormat2.format(incomingFormat.parse(data.getDate()));

            if(position!=0)
            prevDate = localFormat1.format(incomingFormat.parse(dataList.get(position-1).getDate()));

            holder.textDate.setText((currentDate) +", "+ (currentTime));


            holder.textReceivedFrom.setText(data.getReceiver());
            String userName = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_FIRST_NAME, context) + " " + WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_LAST_NAME, context);

            Log.w("TransactionType",data.getType());
            if(data.getType().equalsIgnoreCase("Deposit") || (data.getType().equalsIgnoreCase("Transfer") && !userName.equalsIgnoreCase( data.getSender() ) ) ) {
                holder.textAmount.setText("+ UGX "+ NumberFormat.getInstance().format(data.getAmount())+"");
                holder.textAmount.setTextColor(Color.parseColor("#2E84BE"));
                if(data.getSender()!=null){
                    holder.initials.setText(getNameInitials(data.getSender()));
                    holder.textPaidTo.setText(data.getSender());
                }
                else{
                    holder.initials.setText(getNameInitials(data.getReceiver()));
                    holder.textPaidTo.setText(data.getReceiver());
                }
            }
            else {
                holder.textAmount.setText("- UGX "+ NumberFormat.getInstance().format(data.getAmount())+"");
                holder.textAmount.setTextColor(Color.parseColor("#dc4436"));
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
            if ( name.charAt(i)==' ' && i+1 < name.length() && name.charAt(i+1)!=' '){
                //if i+1==name.length() you will have an indexboundofexception
                //add the initials
                ini+=name.charAt(i+1);
            }
        }
        //after getting "ync" => return "YNC"
        return ini.toUpperCase();
    }
}
