package com.cabral.emaishapay.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.fragments.WalletHomeFragment;
import com.cabral.emaishapay.models.TransactionModel;

import java.util.List;
import java.util.Random;

public class WalletTransactionAdapter extends RecyclerView.Adapter<WalletTransactionAdapter.MyViewHolder> {
    private static final String TAG = "WalletTransaction";
    private Context context;
    private List<TransactionModel> dataList;

    public WalletTransactionAdapter(Context context, List<TransactionModel> dataList) {
        this.context = context;
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

        // Generate random ARGB colors
        Random rnd = new Random();
        int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

        // Set the generated color as the background for name initials
        holder.initials.getBackground().setColorFilter(currentColor, PorterDuff.Mode.SRC_OVER);
        holder.initials.setText(model.getInitials());
        holder.userName.setText(model.getUserName());
        holder.date.setText(model.getDate());
        holder.amount.setText(String.format("UGX %s", model.getAmount()));

        if (model.getAmount().charAt(0) == '+') {
            // Set the generated color as the background for name initials
            holder.amount.setTextColor(Color.parseColor("#2E84BE"));
        } else {
            holder.amount.setTextColor(Color.parseColor("#dc4436"));
        }
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
