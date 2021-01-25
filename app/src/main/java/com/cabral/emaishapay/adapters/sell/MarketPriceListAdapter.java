package com.cabral.emaishapay.adapters.sell;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.cabral.emaishapay.R;
import com.cabral.emaishapay.database.DbHandlerSingleton;
import com.cabral.emaishapay.models.marketplace.MarketPriceItem;

import java.util.ArrayList;
import java.util.List;

public class MarketPriceListAdapter extends RecyclerView.Adapter<MarketPriceListAdapter.MarketPriceViewHolder> {

    LayoutInflater layoutInflater;
    Context mContext;
    ArrayList<MarketPriceItem> marketPriceArrayList = new ArrayList<>();
    DbHandlerSingleton dbHandler;
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();

    public MarketPriceListAdapter(Context context, List<MarketPriceItem> marketPrices) {
        marketPriceArrayList.addAll(marketPrices);
        mContext = context;
        layoutInflater = LayoutInflater.from(mContext);


        Log.d("MARKET PRICES", marketPriceArrayList.size() + " ");
    }

    @NonNull
    @Override
    public MarketPriceListAdapter.MarketPriceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = layoutInflater.inflate(R.layout.market_price_list_item, parent, false);
        dbHandler = DbHandlerSingleton.getHandlerInstance(mContext);
        MarketPriceListAdapter.MarketPriceViewHolder holder = new MarketPriceListAdapter.MarketPriceViewHolder(view);
        return holder;
    }

    public void appendList(ArrayList<MarketPriceItem> marketPrices) {

        this.marketPriceArrayList.addAll(marketPrices);
        notifyDataSetChanged();
    }

    public void addMarketPrice(MarketPriceItem marketPrice) {
        this.marketPriceArrayList.add(marketPrice);
        notifyItemChanged(getItemCount());
    }

    public void changeList(ArrayList<MarketPriceItem> marketPrices) {

        this.marketPriceArrayList.clear();
        this.marketPriceArrayList.addAll(marketPrices);

        notifyDataSetChanged();
    }

    public ArrayList<MarketPriceItem> getMarketPriceItemArrayList(){
        return marketPriceArrayList;
    }

    public void addMarketPriceItem(MarketPriceItem marketPriceItem){
        this.marketPriceArrayList.add(marketPriceItem);
        notifyItemChanged(getItemCount());
    }


    @Override
    public void onBindViewHolder(@NonNull final MarketPriceListAdapter.MarketPriceViewHolder holder, int position) {

        MarketPriceItem marketPriceItem = marketPriceArrayList.get(position);
        holder.marketNameTextView.setText(marketPriceItem.getTitle());


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
        return marketPriceArrayList.size();
    }


    public class MarketPriceViewHolder extends RecyclerView.ViewHolder {

        TextView marketNameTextView;
        RecyclerView recyclerView;

        public MarketPriceViewHolder(View itemView) {
            super(itemView);
            marketNameTextView = itemView.findViewById(R.id.market_name);
            recyclerView = itemView.findViewById(R.id.market_price_recyc);


        }

    }
}