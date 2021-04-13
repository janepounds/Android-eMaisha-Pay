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

    private String order_status;


    public ShopOrderFts(int rowId, String order_date, String order_type, String order_payment_method, String order_status) {
        this.rowId = rowId;
        this.order_date = order_date;
        this.order_type = order_type;
        this.order_payment_method = order_payment_method;
        this.order_status = order_status;
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
}
