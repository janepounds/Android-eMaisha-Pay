package com.cabral.emaishapay.adapters.sell;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.cabral.emaishapay.R;
import com.cabral.emaishapay.models.marketplace.MarketPriceItem;

import java.util.ArrayList;

public class MarketPriceItemAdapter extends RecyclerView.Adapter<MarketPriceItemAdapter.MarketPriceItemViewHolder> {
    private final Context context;
    private final RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private final ArrayList<MarketPriceItem> marketPriceItemArrayList;

    public static class MarketPriceItemViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public RecyclerView recyclerView;

        public MarketPriceItemViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.market_name);
            recyclerView = itemView.findViewById(R.id.market_price_recyc);

        }
    }

    public MarketPriceItemAdapter(Context context, ArrayList<MarketPriceItem> marketPriceItemArrayList) {
        this.context = context;
        this.marketPriceItemArrayList = marketPriceItemArrayList;
    }

    @NonNull
    @Override
    public MarketPriceItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.market_price_list_item, parent, false);

        return new MarketPriceItemViewHolder(v);
    }

    //add market price item
    public void addMarketPriceItem(MarketPriceItem marketPriceItem){
        this.marketPriceItemArrayList.add(marketPriceItem);
        notifyItemChanged(getItemCount());
    }

    //get list
    public ArrayList<MarketPriceItem> getMarketPriceItemArrayList(){

        return marketPriceItemArrayList;
    }

    //change list
    public void changeList(ArrayList<MarketPriceItem> filteredList) {
        this.marketPriceItemArrayList.clear();
        this.marketPriceItemArrayList.addAll(filteredList);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull MarketPriceItemViewHolder holder, int position) {
        MarketPriceItem marketPriceItem = marketPriceItemArrayList.get(position);

        holder.name.setText(marketPriceItem.getTitle());

        // Create a layout manager with initial
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(holder.recyclerView.getContext(),
                LinearLayoutManager.VERTICAL,
                false);

        linearLayoutManager.setInitialPrefetchItemCount(marketPriceItem.getMarketPriceSubItemList().size());

        // Create sub item view adapter
        MarketPriceSubItemAdapter marketPriceSubItemAdapter = new MarketPriceSubItemAdapter(marketPriceItem.getMarketPriceSubItemList());

        holder.recyclerView.setLayoutManager(linearLayoutManager);
        holder.recyclerView.setAdapter(marketPriceSubItemAdapter);
        holder.recyclerView.setRecycledViewPool(viewPool);
    }

    @Override
    public int getItemCount() {
        return marketPriceItemArrayList.size();
    }



}
