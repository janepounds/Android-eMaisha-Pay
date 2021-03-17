package com.cabral.emaishapay.adapters.Shop;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.ShopActivity;
import com.cabral.emaishapay.fragments.BusinessInformationFragment;
import com.cabral.emaishapay.fragments.shop_fragment.OnlineOrderDetailsFragment;

import java.util.HashMap;
import java.util.List;

public class OnlineOrdersAdapter extends RecyclerView.Adapter<com.cabral.emaishapay.adapters.Shop.OnlineOrdersAdapter.MyViewHolder> {
    Context context;
    private List<HashMap<String, String>> orderData;
    public OnlineOrdersAdapter(Context context, List<HashMap<String, String>> orderData) {
        this.context = context;
        this.orderData = orderData;
    }

    @NonNull
    @Override
    public com.cabral.emaishapay.adapters.Shop.OnlineOrdersAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.online_order_item, parent, false);
        return new com.cabral.emaishapay.adapters.Shop.OnlineOrdersAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull com.cabral.emaishapay.adapters.Shop.OnlineOrdersAdapter.MyViewHolder holder, int position) {
        String customer_name = orderData.get(position).get("customer_name");
        String order_status = orderData.get(position).get("order_status");
        String customer_address = orderData.get(position).get("customer_address");


        holder.txt_customer_name.setText(customer_name);
        holder.txt_order_status.setText(order_status);
        holder.txt_customer_address.setText(customer_address);


    }

    @Override
    public int getItemCount() {
        return orderData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txt_customer_name, txt_customer_address, txt_order_status;
        ImageView imgDelete;

        public MyViewHolder(View itemView) {
            super(itemView);

            txt_customer_name = itemView.findViewById(R.id.txt_order_customer_name);
            txt_customer_address = itemView.findViewById(R.id.txt_order_customer_address);
            txt_order_status = itemView.findViewById(R.id.txt_order_status);

            itemView.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {

            Bundle bundle = new Bundle();
            bundle.putString("order_id", orderData.get(getAdapterPosition()).get("invoice_id"));
            bundle.putString("customer_name", orderData.get(getAdapterPosition()).get("customer_name"));
            bundle.putString("order_date", orderData.get(getAdapterPosition()).get("order_date"));
            bundle.putString("order_time", orderData.get(getAdapterPosition()).get("order_time"));
            bundle.putString("order_status", orderData.get(getAdapterPosition()).get("order_status"));
            bundle.putString("storage_status", orderData.get(getAdapterPosition()).get("storage_status"));
            bundle.putString("customer_address", orderData.get(getAdapterPosition()).get("customer_address"));
            bundle.putString("customer_cell", orderData.get(getAdapterPosition()).get("customer_cell"));
            bundle.putString("customer_email", orderData.get(getAdapterPosition()).get("customer_email"));
            bundle.putString("delivery_fee", orderData.get(getAdapterPosition()).get("delivery_fee"));


            Fragment fragment = new OnlineOrderDetailsFragment();
            fragment.setArguments(bundle);
            FragmentManager fragmentManager=((ShopActivity) context).getSupportFragmentManager();

            if (((ShopActivity) context).currentFragment != null)
                fragmentManager.beginTransaction()
                        .hide(((ShopActivity) context).currentFragment)
                        .add(R.id.nav_host_fragment3, fragment)
                        .addToBackStack(null).commit();
            else
                fragmentManager.beginTransaction()
                        .add(R.id.nav_host_fragment3, fragment)
                        .addToBackStack(null).commit();
        }
            

    }
}
