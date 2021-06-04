package com.cabral.emaishapay.adapters.Shop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.network.db.entities.ShopOrderProducts;

import java.util.HashMap;
import java.util.List;

public class OnlineOrderProductsAdapter extends RecyclerView.Adapter<OnlineOrderProductsAdapter.MyViewHolder> {


    Context context;
    private final List<ShopOrderProducts> orderData;

    public OnlineOrderProductsAdapter(Context context, List<ShopOrderProducts> orderData) {

        this.context = context;
        this.orderData = orderData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.online_order_detail_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.txt_product_name.setText(orderData.get(position).getProduct_qty() + " x " + orderData.get(position).getProduct_name());

    }

    @Override
    public int getItemCount() {
        return orderData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt_product_name;


        public MyViewHolder(View itemView) {
            super(itemView);

            txt_product_name = itemView.findViewById(R.id.txt_online_order_product);


        }


    }


}