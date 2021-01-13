package com.cabral.emaishapay.models.marketplace;

public class MarketPriceSubItem {
    private String market;
    private String retail;
    private String wholesale;

    public MarketPriceSubItem(String market, String retail, String wholesale) {
        this.market = market;
        this.retail = retail;
        this.wholesale = wholesale;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getRetail() {
        return retail;
    }

    public void setRetail(String retail) {
        this.retail = retail;
    }

    public String getWholesale() {
        return wholesale;
    }

    public void setWholesale(String wholesale) {
        this.wholesale = wholesale;
    }

    public MarketPriceSubItem() {
    }
}
