package com.cabral.emaishapay.adapters.buyInputsAdapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.cabral.emaishapay.models.product_model.ProductDetails;

import java.util.List;

public class ProductDealsAdapter extends RecyclerView.Adapter<ProductDealsAdapter.MyViewHolder>{
    private List<ProductDetails> productList;
    public ProductDealsAdapter(List<ProductDetails> productList){
        this.productList = productList;

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
