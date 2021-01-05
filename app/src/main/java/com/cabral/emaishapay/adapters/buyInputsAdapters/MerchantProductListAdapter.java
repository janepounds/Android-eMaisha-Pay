package com.cabral.emaishapay.adapters.buyInputsAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.cabral.emaishapay.R;

import java.util.ArrayList;
import java.util.Map;

public class MerchantProductListAdapter  extends RecyclerView.Adapter<MerchantProductListAdapter.MyViewHolder> {

    private final Map<String, String[]> productList;
    private  final ArrayList<String> productNameList;
    private final Context context;

    public MerchantProductListAdapter(Context context, ArrayList<String> productNameList, Map<String, String[]> productList) {
        this.context = context;
        this.productList = productList;
        this.productNameList = productNameList;
    }
    @NonNull
    @Override
    public MerchantProductListAdapter.MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        // Inflate the custom layout
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.buy_inputs_merchant_product_card, parent, false);

        return new MerchantProductListAdapter.MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final String product = this.productNameList.get(position);

        int productprice=0, productqty=1;
        if(this.productList.get(product)!=null){
            productprice=Integer.parseInt(this.productList.get(product)[0]);
            productqty=Integer.parseInt(this.productList.get(product)[1]);
        }

        holder.productPrice.setText( productprice>0? context.getString(R.string.defaultcurrency)+" "+productprice+" X "+productqty : "Not Available");

        holder.productName.setText(product);
    }

    @Override
    public int getItemCount() {
        return this.productNameList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView productName, productPrice;

        public MyViewHolder(final View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
        }
    }
}
