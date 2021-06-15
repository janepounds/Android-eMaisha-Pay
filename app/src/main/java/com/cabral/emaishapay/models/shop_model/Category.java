package com.cabral.emaishapay.models.shop_model;

public class Category {
    private final Integer categories_id;
    private final String categories_slug;

    public Category(Integer categories_id, String categories_slug) {
        this.categories_id = categories_id;
        this.categories_slug = categories_slug;
    }

    public Integer getCategories_id() {
        return categories_id;
    }

    public String getCategories_slug() {
        return categories_slug;
    }
}
