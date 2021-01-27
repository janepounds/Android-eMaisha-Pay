package com.cabral.emaishapay.models.shop_model;

import java.util.List;

public class ProductResponse {
    private List<Product> Products;

    public ProductResponse(List<Product> products) {
        Products = products;
    }

    public List<Product> getProducts() {
        return Products;
    }
}
