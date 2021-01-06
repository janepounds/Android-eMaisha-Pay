package com.cabral.emaishapay.adapters.buyInputsAdapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

public class PopularProductsAdapter extends RecyclerView.Adapter<PopularProductsAdapter.MyViewHolder>{

    public PopularProductsAdapter(){

        }

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        // Inflate the custom layout


       return new PopularProductsAdapter.MyViewHolder(parent);
        }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position){
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
