package com.cabral.emaishapay.network.db.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class EcProductCart implements Parcelable {
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

    public EcProductCart(int cart_id, String product_id, String product_weight, String product_weight_unit, String product_price, int product_qty) {
        this.cart_id = cart_id;
        this.product_id = product_id;
        this.product_weight = product_weight;
        this.product_weight_unit = product_weight_unit;
        this.product_price = product_price;
        this.product_qty = product_qty;
    }

    protected EcProductCart(Parcel in) {
        cart_id = in.readInt();
        product_id = in.readString();
        product_weight = in.readString();
        product_weight_unit = in.readString();
        product_price = in.readString();
        product_qty = in.readInt();
    }

    @Ignore
    public EcProductCart() {

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(cart_id);
        dest.writeString(product_id);
        dest.writeString(product_weight);
        dest.writeString(product_weight_unit);
        dest.writeString(product_price);
        dest.writeInt(product_qty);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<EcProductCart> CREATOR = new Creator<EcProductCart>() {
        @Override
        public EcProductCart createFromParcel(Parcel in) {
            return new EcProductCart(in);
        }

        @Override
        public EcProductCart[] newArray(int size) {
            return new EcProductCart[size];
        }
    };

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

