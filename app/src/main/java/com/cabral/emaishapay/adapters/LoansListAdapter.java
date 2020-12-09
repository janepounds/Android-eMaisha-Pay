package com.cabral.emaishapay.adapters;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.models.LoanApplication;
import com.cabral.emaishapay.singletons.WalletSettingsSingleton;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class LoansListAdapter extends RecyclerView.Adapter<com.cabral.emaishapay.adapters.LoansListAdapter.MyViewHolder> {
    private List<LoanApplication> dataList;

    public LoansListAdapter(List<LoanApplication> dataList) {
        this.dataList = dataList;
    }


    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loan_card, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        LoanApplication data = dataList.get(position);

        holder.numberTxt.setText(String.format("%04d", Integer.parseInt(data.getId())));

        SimpleDateFormat localFormat = new SimpleDateFormat(WalletSettingsSingleton.getInstance().getDateFormat().replace("mm", "MM"), Locale.ENGLISH);
        localFormat.setTimeZone(TimeZone.getDefault());
        String currentDateandTime = null, prevDate = null;

        try {
            SimpleDateFormat incomingFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            incomingFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            currentDateandTime = localFormat.format(incomingFormat.parse(data.getRequestDate()));

            if (position > 0)
                prevDate = localFormat.format(incomingFormat.parse(dataList.get(position - 1).getRequestDate()));


            holder.dateTxt.setText(currentDateandTime);
            if (currentDateandTime.equals(prevDate + ""))
                holder.dateTxt.setVisibility(View.GONE);
            else
                holder.dateTxt.setVisibility(View.VISIBLE);

            Log.d("DATE ", data.getRequestDate() + " => " + WalletSettingsSingleton.getInstance().getDateFormat());

            holder.amountTxt.setText("UGX " + NumberFormat.getInstance().format(data.getAmount()));
            holder.statusTxt.setText(data.generateStatus());

            if (data.getDueDate() != null) {

                holder.dueDateTxt.setText(data.getDueDate());
            } else {
                holder.dueDateTxt.setText("N/A");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView numberTxt, dateTxt, amountTxt, dueDateTxt, statusTxt;

        ConstraintLayout Layout;

        public MyViewHolder(View v) {
            super(v);
            numberTxt = v.findViewById(R.id.text_view_loan_application_number);
            dateTxt = v.findViewById(R.id.text_view_loan_application_date);
            amountTxt = v.findViewById(R.id.text_view_loan_application_amount);
            dueDateTxt = v.findViewById(R.id.text_view_loan_application_due_on);
            statusTxt = v.findViewById(R.id.text_view_loan_application_status);
            Layout = v.findViewById(R.id.card_layout_id);

            Layout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            LoanApplication transaction = dataList.get(getAdapterPosition());

            NavController navController = Navigation.findNavController(v);

            Bundle bundle = new Bundle();
            bundle.putSerializable("loanApplication", transaction);
            navController.navigate(R.id.action_walletLoansListFragment_to_walletLoanStatusPreview,bundle);
        }
    }
}
