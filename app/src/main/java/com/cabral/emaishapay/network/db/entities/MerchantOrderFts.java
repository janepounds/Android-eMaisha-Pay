package com.cabral.emaishapay.network.db.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Fts4;
import androidx.room.PrimaryKey;


@Entity(tableName = "ShopOrderFts")
@Fts4(contentEntity = MerchantOrder.class)
public class MerchantOrderFts {
    @PrimaryKey
    @ColumnInfo(name = "rowid")
    private final int rowId;
    private String order_date;
    private String order_type;
    private String order_payment_method;
    private String order_status;
//    private String customer_name;
//    private String customer_address;
//    private String customer_cell;
//    private String customer_email;

    public MerchantOrderFts(int rowId, String order_date, String order_type, String order_payment_method,
                            String order_status/*, String customer_name, String customer_address, String customer_cell, String customer_email */) {
        this.rowId = rowId;
        this.order_date = order_date;
        this.order_type = order_type;
        this.order_payment_method = order_payment_method;
        this.order_status = order_status;
//        this.customer_name = customer_name;
//        this.customer_address = customer_address;
//        this.customer_cell = customer_cell;
//        this.customer_email = customer_email;
    }

    public int getRowId() {
        return rowId;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getOrder_type() {
        return order_type;
    }

    public void setOrder_type(String order_type) {
        this.order_type = order_type;
    }

    public String getOrder_payment_method() {
        return order_payment_method;
    }

    public void setOrder_payment_method(String order_payment_method) {
        this.order_payment_method = order_payment_method;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }
//
//    public String getCustomer_name() {
//        return customer_name;
//    }
//
//    public void setCustomer_name(String customer_name) {
//        this.customer_name = customer_name;
//    }
//
//    public String getCustomer_address() {
//        return customer_address;
//    }
//
//    public void setCustomer_address(String customer_address) {
//        this.customer_address = customer_address;
//    }
//
//    public String getCustomer_cell() {
//        return customer_cell;
//    }
//
//    public void setCustomer_cell(String customer_cell) {
//        this.customer_cell = customer_cell;
//    }
//
//    public String getCustomer_email() {
//        return customer_email;
//    }
//
//    public void setCustomer_email(String customer_email) {
//        this.customer_email = customer_email;
//    }
}
