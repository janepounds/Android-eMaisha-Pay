package com.cabral.emaishapay.network.db.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class EcOrderDetails implements Parcelable {
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

    public EcOrderDetails(int order_details_id, String product_name, String product_image, String product_order_date) {
        this.order_details_id = order_details_id;
        this.product_name = product_name;
        this.product_image = product_image;
        this.product_order_date = product_order_date;
    }

    protected EcOrderDetails(Parcel in) {
        order_details_id = in.readInt();
        invoice_id = in.readString();
        product_name = in.readString();
        product_weight = in.readString();
        product_qty = in.readString();
        product_price = in.readString();
        product_image = in.readString();
        product_order_date = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(order_details_id);
        dest.writeString(invoice_id);
        dest.writeString(product_name);
        dest.writeString(product_weight);
        dest.writeString(product_qty);
        dest.writeString(product_price);
        dest.writeString(product_image);
        dest.writeString(product_order_date);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<EcOrderDetails> CREATOR = new Creator<EcOrderDetails>() {
        @Override
        public EcOrderDetails createFromParcel(Parcel in) {
            return new EcOrderDetails(in);
        }

        @Override
        public EcOrderDetails[] newArray(int size) {
            return new EcOrderDetails[size];
        }
    };

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

