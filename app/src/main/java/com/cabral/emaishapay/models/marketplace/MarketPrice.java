package com.cabral.emaishapay.models.marketplace;

public class MarketPrice {

    public int id;

    public String crop;

    public String market;

    public String retail;

    public String wholesale;

    public MarketPrice() {
    }

    public MarketPrice(String crop, String market, String retail, String wholesale) {
        this.crop = crop;
        this.market = market;
        this.retail = retail;
        this.wholesale = wholesale;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCrop() {
        return crop;
    }

    public void setCrop(String crop) {
        this.crop = crop;
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
}
