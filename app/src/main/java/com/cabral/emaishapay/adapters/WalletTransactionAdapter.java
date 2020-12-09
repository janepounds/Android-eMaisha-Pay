package com.cabral.emaishapay.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.models.TransactionModel;

import java.util.List;

public class WalletTransactionAdapter extends RecyclerView.Adapter<WalletTransactionAdapter.MyViewHolder> {
    private List<TransactionModel> dataList;

    public WalletTransactionAdapter(List<TransactionModel> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wallet_transaction_card, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TransactionModel model = dataList.get(position);

        holder.initials.setText(model.getInitials());
        holder.userName.setText(model.getUserName());
        holder.date.setText(model.getDate());
        holder.amount.setText(model.getAmount());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView initials, userName, date, amount;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            initials = itemView.findViewById(R.id.initials);
            userName = itemView.findViewById(R.id.user_name);
            date = itemView.findViewById(R.id.date);
            amount = itemView.findViewById(R.id.amount);
        }
    }
}
