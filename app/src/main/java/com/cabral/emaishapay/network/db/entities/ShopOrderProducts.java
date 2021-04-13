package com.cabral.emaishapay.network.db.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {
        @ForeignKey(entity = ShopOrder.class,
                parentColumns = "order_id",
                childColumns = "product_order_id",
                onDelete = ForeignKey.CASCADE)},
        indices = {@Index(value = "product_order_id")
        })
public class ShopOrderProducts implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int order_details_id;
    @ColumnInfo(name = "product_order_id")
    private String product_order_id;
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

    public ShopOrderProducts(int order_details_id, String product_name, String product_image, String product_order_date) {
        this.order_details_id = order_details_id;
        this.product_name = product_name;
        this.product_image = product_image;
        this.product_order_date = product_order_date;
    }

    protected ShopOrderProducts(Parcel in) {
        order_details_id = in.readInt();
        product_order_id = in.readString();
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
        dest.writeString(product_order_id);
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

    public static final Creator<ShopOrderProducts> CREATOR = new Creator<ShopOrderProducts>() {
        @Override
        public ShopOrderProducts createFromParcel(Parcel in) {
            return new ShopOrderProducts(in);
        }

        @Override
        public ShopOrderProducts[] newArray(int size) {
            return new ShopOrderProducts[size];
        }
    };

    public String getProduct_order_id() {
        return product_order_id;
    }

    public void setProduct_order_id(String product_order_id) {
        this.product_order_id = product_order_id;
    }

    public int getOrder_details_id() {
        return order_details_id;
    }

    public void setOrder_details_id(int order_details_id) {
        this.order_details_id = order_details_id;
    }

    public String getOrder_id() {
        return product_order_id;
    }

    public void setOrder_id(String order_id) {
        this.product_order_id = order_id;
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

