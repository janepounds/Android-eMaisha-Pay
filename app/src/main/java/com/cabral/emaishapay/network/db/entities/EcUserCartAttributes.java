package com.cabral.emaishapay.network.db.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(tableName = "EcUserCartAttributes",foreignKeys = {
        @ForeignKey(entity = EcUserCart.class,
                parentColumns = "cart_id",
                childColumns = "cart_table_id",
                onDelete = ForeignKey.CASCADE)},
        indices = {@Index(value = "cart_table_id")
        })
public class EcUserCartAttributes implements Parcelable {

    private String attribute_option_id;
    private String attribute_option_name;
    private String attribute_value_id;
    private String attribute_value_name;
    private String attribute_value_price;
    private String attribute_value_prefix;
    private String attribute_products_id;
    private int cart_table_id;

    public EcUserCartAttributes(String attribute_option_id, String attribute_option_name, String attribute_value_id, String attribute_value_name, String attribute_value_price, String attribute_value_prefix, String attribute_products_id, int cart_table_id) {
        this.attribute_option_id = attribute_option_id;
        this.attribute_option_name = attribute_option_name;
        this.attribute_value_id = attribute_value_id;
        this.attribute_value_name = attribute_value_name;
        this.attribute_value_price = attribute_value_price;
        this.attribute_value_prefix = attribute_value_prefix;
        this.attribute_products_id = attribute_products_id;
        this.cart_table_id = cart_table_id;
    }

    protected EcUserCartAttributes(Parcel in) {
        attribute_option_id = in.readString();
        attribute_option_name = in.readString();
        attribute_value_id = in.readString();
        attribute_value_name = in.readString();
        attribute_value_price = in.readString();
        attribute_value_prefix = in.readString();
        attribute_products_id = in.readString();
        cart_table_id = in.readInt();
    }

    public static final Creator<EcUserCartAttributes> CREATOR = new Creator<EcUserCartAttributes>() {
        @Override
        public EcUserCartAttributes createFromParcel(Parcel in) {
            return new EcUserCartAttributes(in);
        }

        @Override
        public EcUserCartAttributes[] newArray(int size) {
            return new EcUserCartAttributes[size];
        }
    };

    public String getAttribute_option_id() {
        return attribute_option_id;
    }

    public void setAttribute_option_id(String attribute_option_id) {
        this.attribute_option_id = attribute_option_id;
    }

    public String getAttribute_option_name() {
        return attribute_option_name;
    }

    public void setAttribute_option_name(String attribute_option_name) {
        this.attribute_option_name = attribute_option_name;
    }

    public String getAttribute_value_id() {
        return attribute_value_id;
    }

    public void setAttribute_value_id(String attribute_value_id) {
        this.attribute_value_id = attribute_value_id;
    }

    public String getAttribute_value_name() {
        return attribute_value_name;
    }

    public void setAttribute_value_name(String attribute_value_name) {
        this.attribute_value_name = attribute_value_name;
    }

    public String getAttribute_value_price() {
        return attribute_value_price;
    }

    public void setAttribute_value_price(String attribute_value_price) {
        this.attribute_value_price = attribute_value_price;
    }

    public String getAttribute_value_prefix() {
        return attribute_value_prefix;
    }

    public void setAttribute_value_prefix(String attribute_value_prefix) {
        this.attribute_value_prefix = attribute_value_prefix;
    }

    public String getAttribute_products_id() {
        return attribute_products_id;
    }

    public void setAttribute_products_id(String attribute_products_id) {
        this.attribute_products_id = attribute_products_id;
    }

    public int getCart_table_id() {
        return cart_table_id;
    }

    public void setCart_table_id(int cart_table_id) {
        this.cart_table_id = cart_table_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(attribute_option_id);
        dest.writeString(attribute_option_name);
        dest.writeString(attribute_value_id);
        dest.writeString(attribute_value_name);
        dest.writeString(attribute_value_price);
        dest.writeString(attribute_value_prefix);
        dest.writeString(attribute_products_id);
        dest.writeInt(cart_table_id);
    }
}
