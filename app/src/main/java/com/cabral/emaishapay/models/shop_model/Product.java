package com.cabral.emaishapay.models.shop_model;

public class Product {
    private Integer products_id;
    private String products_slug;
    private String products_name;
    private double products_weight;
    private String products_weight_unit;
    private double products_price;
    private String measure_id;
    private String imageUrl;

    public Product(Integer products_id, String products_slug, String products_name, double products_weight, String products_weight_unit, double products_price, String measure_id, String imageUrl) {
        this.products_id = products_id;
        this.products_slug = products_slug;
        this.products_name = products_name;
        this.products_weight = products_weight;
        this.products_weight_unit = products_weight_unit;
        this.products_price = products_price;
        this.measure_id = measure_id;
        this.imageUrl = imageUrl;
    }

    public Integer getProducts_id() {
        return products_id;
    }

    public String getProducts_slug() {
        return products_slug;
    }

    public String getProducts_name() {
        return products_name;
    }

    public double getProducts_weight() {
        return products_weight;
    }

    public String getProducts_weight_unit() {
        return products_weight_unit;
    }

    public double getProducts_price() {
        return products_price;
    }

    public String getMeasure_id() {
        return measure_id;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
