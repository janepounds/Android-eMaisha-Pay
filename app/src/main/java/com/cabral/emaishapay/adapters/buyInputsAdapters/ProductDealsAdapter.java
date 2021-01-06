package com.cabral.emaishapay.adapters.buyInputsAdapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

public class ProductDealsAdapter extends RecyclerView.Adapter<ProductDealsAdapter.MyViewHolder>{
    public ProductDealsAdapter(){

    }

    @Override
    public ProductDealsAdapter.MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        // Inflate the custom layout


        return new ProductDealsAdapter.MyViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(final ProductDealsAdapter.MyViewHolder holder, final int position){
    }

    @Override
    public int getItemCount() {
        return 0;
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(final View itemView) {
            super(itemView);


        }
    }
}
