package com.cabral.emaishapay.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cabral.emaishapay.R;

import com.cabral.emaishapay.models.BeneficiaryResponse;


import java.util.List;

public class BeneficiariesListAdapter extends RecyclerView.Adapter<BeneficiariesListAdapter.MyViewHolder> {
    private List<BeneficiaryResponse.Beneficiaries> dataList;
    private FragmentManager fm;
    Context context;
    private String beneficary_name, initials,cvv,expiry_date,id;


    public  class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView initials,benefaciary_name,date,beneficiary_type,beneficiary_number;
        ImageView close;
        ConstraintLayout constraintLayoutAmount;

        public MyViewHolder(View v, FragmentManager fm) {
            super(v);
            initials = v.findViewById(R.id.initials);
            benefaciary_name = v.findViewById(R.id.user_name);
            beneficiary_type  = v.findViewById(R.id.beneficiary_type);
            beneficiary_number = v.findViewById(R.id.beneficiary_number);
            date = v.findViewById(R.id.date);
            close = v.findViewById(R.id.img_beneficiary_close);
            constraintLayoutAmount = v.findViewById(R.id.layout_amount);

        }


        @Override
        public void onClick(View v) {

        }
    }

    public BeneficiariesListAdapter(List<BeneficiaryResponse.Beneficiaries> dataList, FragmentManager supportFragmentManager) {
        this.dataList = dataList;
        fm=supportFragmentManager;

    }

    @Override
    public BeneficiariesListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wallet_transaction_card,parent,false);
        context=parent.getContext();
        BeneficiariesListAdapter.MyViewHolder holder = new BeneficiariesListAdapter.MyViewHolder(view,fm);
        return holder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(BeneficiariesListAdapter.MyViewHolder holder, int position) {
        BeneficiaryResponse.Beneficiaries data = dataList.get(position);
        holder.constraintLayoutAmount.setVisibility(View.GONE);
        holder.date.setVisibility(View.GONE);
        holder.close.setVisibility(View.VISIBLE);
        holder.beneficiary_type.setVisibility(View.VISIBLE);

        holder.beneficiary_type.setText(data.getBeneficiary_type());
        holder.initials.setText(data.getInitials());
        holder.benefaciary_name.setText(data.getBeneficiary_name());
        holder.beneficiary_number.setText(data.getBeneficiary_number());

    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }



}
