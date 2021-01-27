package com.cabral.emaishapay.models.shop_model;

import java.util.List;

public class CategoriesResponse {
    private List<Category> Categories;

    public CategoriesResponse(List<Category> categories) {
        Categories = categories;
    }

    public List<Category> getCategories() {
        return Categories;
    }


}
