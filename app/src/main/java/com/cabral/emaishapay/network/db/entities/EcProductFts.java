package com.cabral.emaishapay.network.db.entities;

import androidx.room.Entity;
import androidx.room.Fts4;


@Entity(tableName = "EcProductFts")
@Fts4(contentEntity = EcProduct.class)
public class EcProductFts {
    private String product_name;
    private String product_category;
    private String product_description;

    public EcProductFts(String product_name, String product_category, String product_description) {
        this.product_name = product_name;
        this.product_category = product_category;
        this.product_description = product_description;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_category() {
        return product_category;
    }

    public void setProduct_category(String product_category) {
        this.product_category = product_category;
    }

    public String getProduct_description() {
        return product_description;
    }

    public void setProduct_description(String product_description) {
        this.product_description = product_description;
    }
}