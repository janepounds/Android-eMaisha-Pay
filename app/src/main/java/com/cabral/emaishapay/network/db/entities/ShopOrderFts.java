package com.cabral.emaishapay.network.db.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Fts4;
import androidx.room.PrimaryKey;


@Entity(tableName = "ShopOrderFts")
@Fts4(contentEntity = ShopOrder.class)
public class ShopOrderFts {
    @PrimaryKey
    @ColumnInfo(name = "rowid")
    private final int rowId;

    private String order_date;

    private String order_type;

    private String order_payment_method;

    private String customer_name;

    private String order_status;

    private String customer_address;

    private String customer_cell;

    public ShopOrderFts(int rowId, String order_date, String order_type, String order_payment_method, String customer_name, String order_status, String customer_address, String customer_cell) {
        this.rowId = rowId;
        this.order_date = order_date;
        this.order_type = order_type;
        this.order_payment_method = order_payment_method;
        this.customer_name = customer_name;
        this.order_status = order_status;
        this.customer_address = customer_address;
        this.customer_cell = customer_cell;
    }

    public int getRowId() {
        return rowId;
    }

    public String getOrder_date() {
        return order_date;
    }

    public String getOrder_type() {
        return order_type;
    }

    public String getOrder_payment_method() {
        return order_payment_method;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public String getOrder_status() {
        return order_status;
    }

    public String getCustomer_address() {
        return customer_address;
    }

    public String getCustomer_cell() {
        return customer_cell;
    }
}
