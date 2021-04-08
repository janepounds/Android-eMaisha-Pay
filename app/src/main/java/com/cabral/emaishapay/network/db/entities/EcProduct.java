package com.cabral.emaishapay.network.db.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class EcProduct implements Parcelable {
    @PrimaryKey(autoGenerate = false)
     private String product_id;
     @ColumnInfo(name = "product_name")
     private String product_name;
     @ColumnInfo(name = "product_code")
     private String product_code;
    @ColumnInfo(name = "product_category")
     private String product_category;
    @ColumnInfo(name = "product_description")
     private String product_description;
    @ColumnInfo(name = "product_buy_price")
     private String product_buy_price;
    @ColumnInfo(name = "product_sell_price")
     private String product_sell_price;
    @ColumnInfo(name = "product_supplier")
     private String product_supplier;
    @ColumnInfo(name = "product_image")
     private String product_image;
    @ColumnInfo(name = "product_stock")
     private String product_stock;
    @ColumnInfo(name = "product_weight_unit")
     private String product_weight_unit;
    @ColumnInfo(name = "product_weight")
     private String product_weight;
    @ColumnInfo(name = "manufacturer")
     private String manufacturer;
    @ColumnInfo(name = "sync_status",defaultValue = "0")
    private String sync_status;

    public EcProduct(String product_id, String product_name, String product_code, String product_category, String product_description, String product_buy_price, String product_sell_price, String product_supplier, String product_image, String product_stock, String product_weight_unit, String product_weight, String manufacturer, String sync_status) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.product_code = product_code;
        this.product_category = product_category;
        this.product_description = product_description;
        this.product_buy_price = product_buy_price;
        this.product_sell_price = product_sell_price;
        this.product_supplier = product_supplier;
        this.product_image = product_image;
        this.product_stock = product_stock;
        this.product_weight_unit = product_weight_unit;
        this.product_weight = product_weight;
        this.manufacturer = manufacturer;
        this.sync_status = sync_status;
    }

    protected EcProduct(Parcel in) {
        product_id = in.readString();
        product_name = in.readString();
        product_code = in.readString();
        product_category = in.readString();
        product_description = in.readString();
        product_buy_price = in.readString();
        product_sell_price = in.readString();
        product_supplier = in.readString();
        product_image = in.readString();
        product_stock = in.readString();
        product_weight_unit = in.readString();
        product_weight = in.readString();
        manufacturer = in.readString();
        sync_status = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(product_id);
        dest.writeString(product_name);
        dest.writeString(product_code);
        dest.writeString(product_category);
        dest.writeString(product_description);
        dest.writeString(product_buy_price);
        dest.writeString(product_sell_price);
        dest.writeString(product_supplier);
        dest.writeString(product_image);
        dest.writeString(product_stock);
        dest.writeString(product_weight_unit);
        dest.writeString(product_weight);
        dest.writeString(manufacturer);
        dest.writeString(sync_status);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<EcProduct> CREATOR = new Creator<EcProduct>() {
        @Override
        public EcProduct createFromParcel(Parcel in) {
            return new EcProduct(in);
        }

        @Override
        public EcProduct[] newArray(int size) {
            return new EcProduct[size];
        }
    };

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_code() {
        return product_code;
    }

    public void setProduct_code(String product_code) {
        this.product_code = product_code;
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

    public String getProduct_buy_price() {
        return product_buy_price;
    }

    public void setProduct_buy_price(String product_buy_price) {
        this.product_buy_price = product_buy_price;
    }

    public String getProduct_sell_price() {
        return product_sell_price;
    }

    public void setProduct_sell_price(String product_sell_price) {
        this.product_sell_price = product_sell_price;
    }

    public String getProduct_supplier() {
        return product_supplier;
    }

    public void setProduct_supplier(String product_supplier) {
        this.product_supplier = product_supplier;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public String getProduct_stock() {
        return product_stock;
    }

    public void setProduct_stock(String product_stock) {
        this.product_stock = product_stock;
    }

    public String getProduct_weight_unit() {
        return product_weight_unit;
    }

    public void setProduct_weight_unit(String product_weight_unit) {
        this.product_weight_unit = product_weight_unit;
    }

    public String getProduct_weight() {
        return product_weight;
    }

    public void setProduct_weight(String product_weight) {
        this.product_weight = product_weight;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getSync_status() {
        return sync_status;
    }

    public void setSync_status(String sync_status) {
        this.sync_status = sync_status;
    }
}
