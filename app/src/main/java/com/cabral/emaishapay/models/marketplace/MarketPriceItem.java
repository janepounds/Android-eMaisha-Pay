package com.cabral.emaishapay.models.marketplace;

import java.util.ArrayList;
import java.util.List;

public class MarketPriceItem {
    private String title;
    private ArrayList<MarketPriceSubItem> marketPriceSubItemList;

    public MarketPriceItem(String title, ArrayList<MarketPriceSubItem> marketPriceSubItemList) {
        this.title = title;
        this.marketPriceSubItemList = marketPriceSubItemList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<MarketPriceSubItem> getMarketPriceSubItemList() {
        return marketPriceSubItemList;
    }

    public void setMarketPriceSubItemList(ArrayList<MarketPriceSubItem> marketPriceSubItemList) {
        this.marketPriceSubItemList = marketPriceSubItemList;
    }
}
