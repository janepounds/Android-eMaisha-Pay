package com.cabral.emaishapay.adapters.sell;

import android.content.Context;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.models.marketplace.MyProduce;


import java.util.ArrayList;

public class MyProduceListAdapter extends RecyclerView.Adapter<MyProduceListAdapter.MyProduceListViewHolder> {
    private Context context;
    private ArrayList<MyProduce> myProduceArrayList;

    public static class MyProduceListViewHolder extends RecyclerView.ViewHolder {
        public TextView name, variety, quantity, price, date;
        public ImageView image;

        public MyProduceListViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_produce_name);
            variety = itemView.findViewById(R.id.item_produce_variety);
            quantity = itemView.findViewById(R.id.item_produce_quantity);
            price = itemView.findViewById(R.id.item_produce_price);
            date = itemView.findViewById(R.id.item_produce_date);
            image = itemView.findViewById(R.id.item_produce_image);
        }
    }

    public MyProduceListAdapter(Context context, ArrayList<MyProduce> myProduceArrayList) {
        this.context = context;
        this.myProduceArrayList = myProduceArrayList;
    }

    @NonNull
    @Override
    public MyProduceListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_produce_list_item, parent, false);

        return new MyProduceListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyProduceListViewHolder holder, int position) {
        MyProduce myProduce = myProduceArrayList.get(position);

        holder.name.setText(myProduce.getName());
        holder.variety.setText(myProduce.getVariety());
        holder.quantity.setText(myProduce.getQuantity());
        holder.price.setText(myProduce.getPrice());
        holder.date.setText(myProduce.getDate());

        Glide.with(context).load(Base64.decode(myProduce.getImage(), Base64.DEFAULT)).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return myProduceArrayList.size();
    }
}
