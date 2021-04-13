package com.cabral.emaishapay.network.db.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(indices = {@Index(value = {"order_id"},
        unique = true)})
public class ShopOrder implements Parcelable, Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "order_id")
    private String order_id;
    @ColumnInfo(name = "order_date")
    private String order_date;
    @ColumnInfo(name = "order_time")
    private String order_time;
    @ColumnInfo(name = "order_type")
    private String order_type;
    @ColumnInfo(name = "order_payment_method")
    private String order_payment_method;
    @ColumnInfo(name = "customer_name")
    private String customer_name;
    @ColumnInfo(name = "storage_status",defaultValue = "local")
    private String storage_status;
    @ColumnInfo(name = "discount")
    private double discount;
    @ColumnInfo(name = "order_status")
    private String order_status;
    @ColumnInfo(name = "customer_address")
    private String customer_address;
    @ColumnInfo(name = "customer_cell")
    private String customer_cell;
    @ColumnInfo(name = "delivery_fee")
    private String delivery_fee;
    @ColumnInfo(name = "customer_email")
    private String customer_email;

    public ShopOrder(String order_id, String order_date, String order_time, String order_type, String storage_status, String order_payment_method, String customer_name, double discount, String order_status, String customer_address, String customer_cell, String delivery_fee, String customer_email) {
        this.order_id = order_id;
        this.order_date = order_date;
        this.order_time = order_time;
        this.order_type = order_type;
        this.storage_status=storage_status;
        this.order_payment_method = order_payment_method;
        this.customer_name = customer_name;
        this.discount = discount;
        this.order_status = order_status;
        this.customer_address = customer_address;
        this.customer_cell = customer_cell;
        this.delivery_fee = delivery_fee;
        this.customer_email = customer_email;
    }

    protected ShopOrder(Parcel in) {
        order_id = in.readString();
        order_date = in.readString();
        order_time = in.readString();
        order_type = in.readString();
        order_payment_method = in.readString();
        customer_name = in.readString();
        storage_status = in.readString();
        discount = in.readInt();
        order_status = in.readString();
        customer_address = in.readString();
        customer_cell = in.readString();
        delivery_fee = in.readString();
        customer_email = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(order_id);
        dest.writeString(order_date);
        dest.writeString(order_time);
        dest.writeString(order_type);
        dest.writeString(order_payment_method);
        dest.writeString(customer_name);
        dest.writeString(storage_status);
        dest.writeDouble(discount);
        dest.writeString(order_status);
        dest.writeString(customer_address);
        dest.writeString(customer_cell);
        dest.writeString(delivery_fee);
        dest.writeString(customer_email);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ShopOrder> CREATOR = new Creator<ShopOrder>() {
        @Override
        public ShopOrder createFromParcel(Parcel in) {
            return new ShopOrder(in);
        }

        @Override
        public ShopOrder[] newArray(int size) {
            return new ShopOrder[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }


    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getOrder_time() {
        return order_time;
    }

    public void setOrder_time(String order_time) {
        this.order_time = order_time;
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

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getStorage_status() {
        return storage_status;
    }

    public void setStorage_status(String storage_status) {
        this.storage_status = storage_status;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public String getCustomer_address() {
        return customer_address;
    }

    public void setCustomer_address(String customer_address) {
        this.customer_address = customer_address;
    }

    public String getCustomer_cell() {
        return customer_cell;
    }

    public void setCustomer_cell(String customer_cell) {
        this.customer_cell = customer_cell;
    }

    public String getDelivery_fee() {
        return delivery_fee;
    }

    public void setDelivery_fee(String delivery_fee) {
        this.delivery_fee = delivery_fee;
    }

    public String getCustomer_email() {
        return customer_email;
    }

    public void setCustomer_email(String customer_email) {
        this.customer_email = customer_email;
    }
}
