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


import com.cabral.emaishapay.R;
import com.cabral.emaishapay.models.marketplace.MarketPriceSubItem;

import java.util.ArrayList;

public class MarketPriceSubItemAdapter extends RecyclerView.Adapter<MarketPriceSubItemAdapter.MarketPriceSubItemViewHolder> {
    private final ArrayList<MarketPriceSubItem> marketPriceSubItemArrayList;

    public static class MarketPriceSubItemViewHolder extends RecyclerView.ViewHolder {
        public TextView market, retail, wholesale;

        public MarketPriceSubItemViewHolder(@NonNull View itemView) {
            super(itemView);
            market = itemView.findViewById(R.id.market_sub_item);
            retail = itemView.findViewById(R.id.retail_sub_item);
            wholesale = itemView.findViewById(R.id.wholesale_sub_item);
        }
    }

    public MarketPriceSubItemAdapter(ArrayList<MarketPriceSubItem> marketPriceSubItemArrayList) {
        this.marketPriceSubItemArrayList = marketPriceSubItemArrayList;
    }

    @NonNull
    @Override
    public MarketPriceSubItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.market_price_list_sub_item, parent, false);

        return new MarketPriceSubItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MarketPriceSubItemViewHolder holder, int position) {
        MarketPriceSubItem marketPriceSubItem = marketPriceSubItemArrayList.get(position);

        holder.market.setText(marketPriceSubItem.getMarket());
        holder.retail.setText("UGX " + marketPriceSubItem.getRetail());
        holder.wholesale.setText("UGX " + marketPriceSubItem.getWholesale());
    }

    @Override
    public int getItemCount() {
        return marketPriceSubItemArrayList.size();
    }
}
