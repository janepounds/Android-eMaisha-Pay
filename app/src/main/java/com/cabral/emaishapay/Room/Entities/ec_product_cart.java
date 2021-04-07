package com.cabral.emaishapay.Room.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "product_cart")
public class ec_product_cart {
    @PrimaryKey(autoGenerate = true)
    private int cart_id;
    @ColumnInfo(name = "product_id")
    private String product_id;
    @ColumnInfo(name = "product_weight")
    private String product_weight;
    @ColumnInfo(name = "product_weight_unit")
    private String product_weight_unit;
    @ColumnInfo(name = "product_price")
    private String product_price;
    @ColumnInfo(name = "product_qty")
    private int product_qty;

    public ec_product_cart(int cart_id, String product_id, String product_weight, String product_weight_unit, String product_price, int product_qty) {
        this.cart_id = cart_id;
        this.product_id = product_id;
        this.product_weight = product_weight;
        this.product_weight_unit = product_weight_unit;
        this.product_price = product_price;
        this.product_qty = product_qty;
    }

    public int getCart_id() {
        return cart_id;
    }

    public void setCart_id(int cart_id) {
        this.cart_id = cart_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_weight() {
        return product_weight;
    }

    public void setProduct_weight(String product_weight) {
        this.product_weight = product_weight;
    }

    public String getProduct_weight_unit() {
        return product_weight_unit;
    }

    public void setProduct_weight_unit(String product_weight_unit) {
        this.product_weight_unit = product_weight_unit;
    }

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }

    public int getProduct_qty() {
        return product_qty;
    }

    public void setProduct_qty(int product_qty) {
        this.product_qty = product_qty;
    }
}

