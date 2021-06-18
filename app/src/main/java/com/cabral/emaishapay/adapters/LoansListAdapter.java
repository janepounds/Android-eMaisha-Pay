package com.cabral.emaishapay.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.models.LoanApplication;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class LoansListAdapter extends RecyclerView.Adapter<com.cabral.emaishapay.adapters.LoansListAdapter.MyViewHolder> {
    private final List<LoanApplication> dataList;
    Context context;

    public LoansListAdapter(Context context,List<LoanApplication> dataList) {
        this.dataList = dataList;
        this.context=context;
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

        holder.numberTxt.setText(data.getLoan_no());

        SimpleDateFormat localFormat = new SimpleDateFormat(context.getString(R.string.date_format_preffered).replace("mm", "MM"), Locale.ENGLISH);
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

            Log.d("DATE ", data.getRequestDate() + " => " + context.getString(R.string.date_format_preffered));

            holder.amountTxt.setText("UGX " + NumberFormat.getInstance().format(data.getAmount()));
            holder.statusTxt.setText(data.generateStatus());

            //check status
            if(data.generateStatus().equalsIgnoreCase("Rejected")){
                holder.pending.setVisibility(View.GONE);
                holder.rejected.setVisibility(View.VISIBLE);
                holder.appTx.setText("Rejected On");

            }else if(data.generateStatus().equalsIgnoreCase("Pending")){
                holder.pending.setVisibility(View.VISIBLE);
                holder.rejected.setVisibility(View.GONE);

            }else{

                holder.pending.setVisibility(View.GONE);
                holder.completed.setVisibility(View.VISIBLE);
                holder.appTx.setText("Completed On");
            }

//            if (data.getDueDate() != null) {
//
//                holder.dueDateTxt.setText(data.getDueDate());
//            } else {
//                holder.dueDateTxt.setText("N/A");
//            }

            holder.dueDateTxt.setText(data.getInterestRate()+" %");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView numberTxt, dateTxt, amountTxt, dueDateTxt, statusTxt,appTx;

        ConstraintLayout Layout;
        LinearLayout pending,rejected,completed;

        public MyViewHolder(View v) {
            super(v);
            numberTxt = v.findViewById(R.id.text_view_loan_application_number);
            dateTxt = v.findViewById(R.id.text_view_loan_application_date);
            amountTxt = v.findViewById(R.id.text_view_loan_application_amount);
            dueDateTxt = v.findViewById(R.id.text_view_loan_application_due_on);
            statusTxt = v.findViewById(R.id.text_view_loan_application_status);
            Layout = v.findViewById(R.id.card_layout_id);
            pending = v.findViewById(R.id.layout_pending);
            rejected = v.findViewById(R.id.layout_disapproved);
            completed = v.findViewById(R.id.layout_completed_on);
            appTx = v.findViewById(R.id.text_view_applied_on);

            Layout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            LoanApplication transaction = dataList.get(getBindingAdapterPosition());

          //  NavController navController = Navigation.findNavController(v);

            Bundle args = new Bundle();
            args.putSerializable("loanApplication", transaction);

            //To WalletLoanStatusPreview();
            WalletHomeActivity.navController.navigate(R.id.action_walletLoansListFragment_to_walletLoanStatusPreview,args);
        }
    }
}
