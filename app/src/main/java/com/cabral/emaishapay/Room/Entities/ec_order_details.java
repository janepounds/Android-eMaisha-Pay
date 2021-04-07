package com.cabral.emaishapay.Room.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "order_details")
public class ec_order_details {
    @PrimaryKey(autoGenerate = true)
    private int order_details_id;
    @ColumnInfo(name = "invoice_id")
    private String invoice_id;
    @ColumnInfo(name = "product_name")
    private String product_name;
    @ColumnInfo(name = "product_weight")
    private String product_weight;
    @ColumnInfo(name = "product_qty")
    private String product_qty;
    @ColumnInfo(name = "product_price")
    private String product_price;
    @ColumnInfo(name = "product_image")
    private String product_image;
    @ColumnInfo(name = "product_order_date")
    private String product_order_date;

    public ec_order_details(int order_details_id, String product_name, String product_image, String product_order_date) {
        this.order_details_id = order_details_id;
        this.product_name = product_name;
        this.product_image = product_image;
        this.product_order_date = product_order_date;
    }

    public int getOrder_details_id() {
        return order_details_id;
    }

    public void setOrder_details_id(int order_details_id) {
        this.order_details_id = order_details_id;
    }

    public String getInvoice_id() {
        return invoice_id;
    }

    public void setInvoice_id(String invoice_id) {
        this.invoice_id = invoice_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_weight() {
        return product_weight;
    }

    public void setProduct_weight(String product_weight) {
        this.product_weight = product_weight;
    }

    public String getProduct_qty() {
        return product_qty;
    }

    public void setProduct_qty(String product_qty) {
        this.product_qty = product_qty;
    }

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public String getProduct_order_date() {
        return product_order_date;
    }

    public void setProduct_order_date(String product_order_date) {
        this.product_order_date = product_order_date;
    }
}

