package com.cabral.emaishapay.network.db.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class UserCart implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int cart_id;
    public  String products_id;
    public  String products_name;
    public  String products_image;
    public  String products_url;
    public  String product_model;
    public  String products_weight;
    public  String products_weight_unit;
    public  String product_stock;
    public  String product_quantity;
    public  String product_price;
    public  String product_attr_price;
    public  String product_total_price;
    public  String product_final_price;
    public  String products_description;
    public  String categories_id;
    public  String categories_name;
    public  String manufacturers_id;
    public  String manufacturer_name;
    public  String product_taxClassID;
    public  String tax_description;
    public  String tax_class_title;
    public  String tax_class_description;
    public  String is_sale_product;
    public  String cart_date_added;
    @Ignore
    private String selectedProductsWeight;
    @Ignore
    private String selectedProductsWeightUnit;


    protected UserCart(Parcel in) {
        cart_id = in.readInt();
        products_id = in.readString();
        products_name = in.readString();
        products_image = in.readString();
        products_url = in.readString();
        product_model = in.readString();
        products_weight = in.readString();
        products_weight_unit = in.readString();
        product_stock = in.readString();
        product_quantity = in.readString();
        product_price = in.readString();
        product_attr_price = in.readString();
        product_total_price = in.readString();
        product_final_price = in.readString();
        products_description = in.readString();
        categories_id = in.readString();
        categories_name = in.readString();
        manufacturers_id = in.readString();
        manufacturer_name = in.readString();
        product_taxClassID = in.readString();
        tax_description = in.readString();
        tax_class_title = in.readString();
        tax_class_description = in.readString();
        is_sale_product = in.readString();
        cart_date_added = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
       // dest.writeInt(cart_id);
        dest.writeString(products_id);
        dest.writeString(products_name);
        dest.writeString(products_image);
        dest.writeString(products_url);
        dest.writeString(product_model);
        dest.writeString(products_weight);
        dest.writeString(products_weight_unit);
        dest.writeString(product_stock);
        dest.writeString(product_quantity);
        dest.writeString(product_price);
        dest.writeString(product_attr_price);
        dest.writeString(product_total_price);
        dest.writeString(product_final_price);
        dest.writeString(products_description);
        dest.writeString(categories_id);
        dest.writeString(categories_name);
        dest.writeString(manufacturers_id);
        dest.writeString(manufacturer_name);
        dest.writeString(product_taxClassID);
        dest.writeString(tax_description);
        dest.writeString(tax_class_title);
        dest.writeString(tax_class_description);
        dest.writeString(is_sale_product);
        dest.writeString(cart_date_added);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserCart> CREATOR = new Creator<UserCart>() {
        @Override
        public UserCart createFromParcel(Parcel in) {
            return new UserCart(in);
        }

        @Override
        public UserCart[] newArray(int size) {
            return new UserCart[size];
        }
    };

    public int getCart_id() {
        return cart_id;
    }

    public void setCart_id(int cart_id) {
        this.cart_id = cart_id;
    }

    public String getProducts_id() {
        return products_id;
    }

    public void setProducts_id(String products_id) {
        this.products_id = products_id;
    }

    public String getProducts_name() {
        return products_name;
    }

    public void setProducts_name(String products_name) {
        this.products_name = products_name;
    }

    public String getProducts_image() {
        return products_image;
    }

    public void setProducts_image(String products_image) {
        this.products_image = products_image;
    }

    public String getProducts_url() {
        return products_url;
    }

    public void setProducts_url(String products_url) {
        this.products_url = products_url;
    }

    public String getProduct_model() {
        return product_model;
    }

    public void setProduct_model(String product_model) {
        this.product_model = product_model;
    }

    public String getProducts_weight() {
        return products_weight;
    }

    public void setProducts_weight(String products_weight) {
        this.products_weight = products_weight;
    }

    public String getProducts_weight_unit() {
        return products_weight_unit;
    }

    public void setProducts_weight_unit(String products_weight_unit) {
        this.products_weight_unit = products_weight_unit;
    }

    public String getProduct_stock() {
        return product_stock;
    }

    public void setProduct_stock(String product_stock) {
        this.product_stock = product_stock;
    }

    public String getProduct_quantity() {
        return product_quantity;
    }

    public void setProduct_quantity(String product_quantity) {
        this.product_quantity = product_quantity;
    }

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }

    public String getProduct_attr_price() {
        return product_attr_price;
    }

    public void setProduct_attr_price(String product_attr_price) {
        this.product_attr_price = product_attr_price;
    }

    public String getProduct_total_price() {
        return product_total_price;
    }

    public void setProduct_total_price(String product_total_price) {
        this.product_total_price = product_total_price;
    }

    public String getProduct_final_price() {
        return product_final_price;
    }

    public void setProduct_final_price(String product_final_price) {
        this.product_final_price = product_final_price;
    }

    public String getProducts_description() {
        return products_description;
    }

    public void setProducts_description(String products_description) {
        this.products_description = products_description;
    }

    public String getCategories_id() {
        return categories_id;
    }

    public void setCategories_id(String categories_id) {
        this.categories_id = categories_id;
    }

    public String getCategories_name() {
        return categories_name;
    }

    public void setCategories_name(String categories_name) {
        this.categories_name = categories_name;
    }

    public String getManufacturers_id() {
        return manufacturers_id;
    }

    public void setManufacturers_id(String manufacturers_id) {
        this.manufacturers_id = manufacturers_id;
    }

    public String getManufacturer_name() {
        return manufacturer_name;
    }

    public void setManufacturer_name(String manufacturer_name) {
        this.manufacturer_name = manufacturer_name;
    }

    public String getProduct_taxClassID() {
        return product_taxClassID;
    }

    public void setProduct_taxClassID(String product_taxClassID) {
        this.product_taxClassID = product_taxClassID;
    }

    public String getTax_description() {
        return tax_description;
    }

    public void setTax_description(String tax_description) {
        this.tax_description = tax_description;
    }

    public String getTax_class_title() {
        return tax_class_title;
    }

    public void setTax_class_title(String tax_class_title) {
        this.tax_class_title = tax_class_title;
    }

    public String getTax_class_description() {
        return tax_class_description;
    }

    public void setTax_class_description(String tax_class_description) {
        this.tax_class_description = tax_class_description;
    }

    public String getIs_sale_product() {
        return is_sale_product;
    }

    public void setIs_sale_product(String is_sale_product) {
        this.is_sale_product = is_sale_product;
    }

    public String getCart_date_added() {
        return cart_date_added;
    }

    public void setCart_date_added(String cart_date_added) {
        this.cart_date_added = cart_date_added;
    }

    public String getSelectedProductsWeight() {
        return this.selectedProductsWeight;
    }

    public void setSelectedProductsWeight(String selectedProductsWeight) {
        this.selectedProductsWeight = selectedProductsWeight;
    }

    public String getSelectedProductsWeightUnit() {
        return  this.selectedProductsWeightUnit;
    }
    public void setSelectedProductsWeightUnit(String selectedProductsWeightUnit) {
        this.selectedProductsWeightUnit = selectedProductsWeightUnit;
    }

}

