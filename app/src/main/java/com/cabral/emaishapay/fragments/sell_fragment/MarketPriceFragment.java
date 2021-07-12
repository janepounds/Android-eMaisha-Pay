package com.cabral.emaishapay.fragments.sell_fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.adapters.sell.MarketPriceItemAdapter;
import com.cabral.emaishapay.models.marketplace.MarketPrice;
import com.cabral.emaishapay.models.marketplace.MarketPriceItem;
import com.cabral.emaishapay.models.marketplace.MarketPriceSubItem;

import java.util.ArrayList;

public class MarketPriceFragment extends Fragment {
    private static final String TAG = "MarketPriceFragment";
    private Context context;

    private final ArrayList<MarketPrice> marketPriceArrayList = new ArrayList<>();
    private final ArrayList<MarketPriceItem> marketPriceItemArrayList = new ArrayList<>();
    private final ArrayList<MarketPriceItem> marketPriceItemArrayListBackUp = new ArrayList<>();
    private final ArrayList<MarketPriceSubItem> marketPriceSubItemArrayList = new ArrayList<>();

    MarketPriceItemAdapter adapter;

    private RecyclerView recyclerView;
    private Spinner spinner;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_market_price, container, false);

        recyclerView = view.findViewById(R.id.recyclerView_market_price_fragment);
        spinner = view.findViewById(R.id.spinner_market_price_fragment);

//        dbHandler = DbHandlerSingleton.getHandlerInstance(context);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        adapter = new MarketPriceItemAdapter(context, marketPriceItemArrayList);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

//        loadMarketPrices();
//        dbHandler.insertMarketPrice(new MarketPrice("Ginger", "Tororo", "1,000", "600"));
//        dbHandler.insertMarketPrice(new MarketPrice("Millet", "Mbarara", "1,800", "1,500"));
//        dbHandler.insertMarketPrice(new MarketPrice("Milk", "Gulu", "1,000", "800"));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        ((TextView) view).setTextColor(ContextCompat.getColor(context,R.color.colorPrimary));

                    } else {
                        ((TextView) view).setTextColor(ContextCompat.getColor(context,R.color.colorPrimary)); //Change selected text color
                    }
                    ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);//Change selected text size
                } catch (Exception e) {

                }

                if (position != 0) {
                    String selection = parent.getSelectedItem().toString();
                    ArrayList<MarketPriceItem> filteredList = new ArrayList<>();
                    if (marketPriceItemArrayListBackUp.size() == 0) {
                        // cropArrayList.clear();
                        for (MarketPriceItem x : adapter.getMarketPriceItemArrayList()) {
                            marketPriceItemArrayListBackUp.add(x);
                        }
                    }

                    filteredList.clear();
                    marketPriceSubItemArrayList.clear();
                    for (MarketPriceItem x : marketPriceItemArrayListBackUp) {
                        if ((x.getTitle().toLowerCase()).contains(selection.toLowerCase())) {
//                            marketPriceArrayList = dbHandler.filterMarketPrices(x.getTitle());

                            for (int k = 0; k < marketPriceArrayList.size(); k++) {
                                Log.d(TAG, "onItemSelected: Array = " + marketPriceArrayList.get(k));
                                MarketPrice marketPrice = marketPriceArrayList.get(k);
                                Log.d(TAG, "onItemSelected: Name = " + marketPrice.getCrop());

//                                marketPriceSubItemArrayList = dbHandler.filterMarketPriceSubItem(marketPrice.getCrop());

                            }

                            MarketPriceItem marketPriceItem = new MarketPriceItem(x.getTitle(), marketPriceSubItemArrayList);
                            filteredList.add(marketPriceItem);
                        }

                    }

                    adapter.changeList(filteredList);
                }else {
//                    loadMarketPrices();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

//    public void loadMarketPrices(){
//        marketPriceArrayList = dbHandler.getAllMarketPrices();
//
//        for (int k = 0; k < marketPriceArrayList.size(); k++) {
//            Log.d(TAG, "onItemSelected: Array = " + marketPriceArrayList.get(k));
//            MarketPrice marketPrice = marketPriceArrayList.get(k);
//            Log.d(TAG, "onItemSelected: Name = " + marketPrice.getCrop());
//            if(marketPrice.getCrop().equals("Ginger")){
//                marketPriceSubItemArrayList = dbHandler.filterMarketPriceSubItem("Ginger");
//
//            }else if(marketPrice.getCrop().equals("Millet")){
//                marketPriceSubItemArrayList = dbHandler.filterMarketPriceSubItem("Millet");
//            }else if(marketPrice.getCrop().equals("Milk")){
//                marketPriceSubItemArrayList = dbHandler.filterMarketPriceSubItem("Milk");
//            }
//
//            MarketPriceItem marketPriceItem = new MarketPriceItem(marketPrice.getCrop(), marketPriceSubItemArrayList);
//            adapter.addMarketPriceItem(marketPriceItem);
//
//
//
//
//        }
//    }
}
