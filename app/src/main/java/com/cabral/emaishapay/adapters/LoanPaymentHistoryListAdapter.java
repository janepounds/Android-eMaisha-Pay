package com.cabral.emaishapay.adapters;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.models.LoanApplication;
import java.util.List;
public class LoanPaymentHistoryListAdapter extends RecyclerView.Adapter<com.cabral.emaishapay.adapters.LoanPaymentHistoryListAdapter.MyViewHolder> {
    private List<LoanApplication> dataList;

    public LoanPaymentHistoryListAdapter() {

    }


    @Override
    public LoanPaymentHistoryListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_history_card, parent, false);
        LoanPaymentHistoryListAdapter.MyViewHolder holder = new LoanPaymentHistoryListAdapter.MyViewHolder(view);
        return holder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(LoanPaymentHistoryListAdapter.MyViewHolder holder, int position) {


    }


    @Override
    public int getItemCount() {
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public MyViewHolder(View v) {
            super(v);

        }

        @Override
        public void onClick(View v) {

        }
    }
}
