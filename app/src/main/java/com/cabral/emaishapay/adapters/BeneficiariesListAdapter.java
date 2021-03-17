package com.cabral.emaishapay.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cabral.emaishapay.R;

import com.cabral.emaishapay.models.BeneficiaryResponse;


import java.util.List;

public class BeneficiariesListAdapter extends RecyclerView.Adapter<BeneficiariesListAdapter.MyViewHolder> {
    private List<BeneficiaryResponse.Beneficiaries> dataList;
    private FragmentManager fm;
    Context context;


    public  class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        public MyViewHolder(View v, FragmentManager fm) {
            super(v);
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


    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }



}
