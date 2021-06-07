package com.cabral.emaishapay.models.order_model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class OrderProducts implements Parcelable {

    @SerializedName("orders_products_id")
    @Expose
    private Integer ordersProductsId;
    @SerializedName("orders_id")
    @Expose
    private String ordersId;
    @SerializedName("products_id")
    @Expose
    private Integer productsId;
    @SerializedName("products_model")
    @Expose
    private Object productsModel;
    @SerializedName("products_name")
    @Expose
    private String productsName;
    @SerializedName("products_price")
    @Expose
    private String productsPrice;
    @SerializedName("final_price")
    @Expose
    private String finalPrice;
    @SerializedName("products_tax")
    @Expose
    private String productsTax;
    @SerializedName("products_quantity")
    @Expose
    private Integer productsQuantity;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("buy_inputs_categories")
    @Expose
    private List<OrderProductCategory> categories = null;
    @SerializedName("attributes")
    @Expose
    private List<PostProductsAttributes> attributes = null;

    protected OrderProducts(Parcel in) {
        if (in.readByte() == 0) {
            ordersProductsId = null;
        } else {
            ordersProductsId = in.readInt();
        }
        ordersId = in.readString();
        if (in.readByte() == 0) {
            productsId = null;
        } else {
            productsId = in.readInt();
        }
        productsName = in.readString();
        productsPrice = in.readString();
        finalPrice = in.readString();
        productsTax = in.readString();
        if (in.readByte() == 0) {
            productsQuantity = null;
        } else {
            productsQuantity = in.readInt();
        }
        image = in.readString();
        attributes = in.createTypedArrayList(PostProductsAttributes.CREATOR);
    }

    public static final Creator<OrderProducts> CREATOR = new Creator<OrderProducts>() {
        @Override
        public OrderProducts createFromParcel(Parcel in) {
            return new OrderProducts(in);
        }

        @Override
        public OrderProducts[] newArray(int size) {
            return new OrderProducts[size];
        }
    };

    public Integer getOrdersProductsId() {
        return ordersProductsId;
    }

    public void setOrdersProductsId(Integer ordersProductsId) {
        this.ordersProductsId = ordersProductsId;
    }

    public String getOrdersId() {
        return ordersId;
    }

    public void setOrdersId(String ordersId) {
        this.ordersId = ordersId;
    }

    public Integer getProductsId() {
        return productsId;
    }

    public void setProductsId(Integer productsId) {
        this.productsId = productsId;
    }

    public Object getProductsModel() {
        return productsModel;
    }

    public void setProductsModel(Object productsModel) {
        this.productsModel = productsModel;
    }

    public String getProductsName() {
        return productsName;
    }

    public void setProductsName(String productsName) {
        this.productsName = productsName;
    }

    public String getProductsPrice() {
        return productsPrice;
    }

    public void setProductsPrice(String productsPrice) {
        this.productsPrice = productsPrice;
    }

    public String getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(String finalPrice) {
        this.finalPrice = finalPrice;
    }

    public String getProductsTax() {
        return productsTax;
    }

    public void setProductsTax(String productsTax) {
        this.productsTax = productsTax;
    }

    public Integer getProductsQuantity() {
        return productsQuantity;
    }

    public void setProductsQuantity(Integer productsQuantity) {
        this.productsQuantity = productsQuantity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<OrderProductCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<OrderProductCategory> categories) {
        this.categories = categories;
    }

    public List<PostProductsAttributes> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<PostProductsAttributes> attributes) {
        this.attributes = attributes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (ordersProductsId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(ordersProductsId);
        }
        dest.writeString(ordersId);
        if (productsId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(productsId);
        }
        dest.writeString(productsName);
        dest.writeString(productsPrice);
        dest.writeString(finalPrice);
        dest.writeString(productsTax);
        if (productsQuantity == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(productsQuantity);
        }
        dest.writeString(image);
        dest.writeTypedList(attributes);
    }
}
