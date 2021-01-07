package com.cabral.emaishapay.models;

import com.cabral.emaishapay.models.product_model.ProductDetails;

import java.util.ArrayList;
import java.util.List;

public class PopularDealsProduct {
    private List<ProductDetails> dealsList = new ArrayList<>();
    private List<ProductDetails> popularproductList = new ArrayList<>();

    public List<ProductDetails> getDealsList() {
        return dealsList;
    }

    public void setDealsList(List<ProductDetails> dealsList) {
        this.dealsList = dealsList;
    }

    public List<ProductDetails> getPopularproductList() {
        return popularproductList;
    }

    public void setPopularproductList(List<ProductDetails> popularproductList) {
        this.popularproductList = popularproductList;
    }
}
