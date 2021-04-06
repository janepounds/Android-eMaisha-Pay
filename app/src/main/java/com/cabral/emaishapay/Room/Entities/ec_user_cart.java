package com.cabral.emaishapay.Room.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_cart")
public class ec_user_cart {
    @PrimaryKey(autoGenerate = true)
    private int cart_id;
    @ColumnInfo(name = "products_id")
    private int products_id;
    @ColumnInfo(name = "products_name")
    private String products_name;
    @ColumnInfo(name = "products_image")
    private String products_image;
    @ColumnInfo(name = "products_url")
    private String products_url;
    @ColumnInfo(name = "product_model")
    private String product_model;
    @ColumnInfo(name = "products_weight")
    private String products_weight;
    @ColumnInfo(name = "products_weight_unit")
    private String products_weight_unit;
    @ColumnInfo(name = "product_stock")
    private int product_stock;
    @ColumnInfo(name = "product_quantity")
    private int product_quantity;
    @ColumnInfo(name = "product_price")
    private String product_price;
    @ColumnInfo(name = "product_attr_price")
    private String product_attr_price;
    @ColumnInfo(name = "product_total_price")
    private String product_total_price;
    @ColumnInfo(name = "product_final_price")
    private String product_final_price;
    @ColumnInfo(name = "products_description")
    private String products_description;
    @ColumnInfo(name = "categories_id")
    private String categories_id;
    @ColumnInfo(name = "categories_name")
    private String categories_name;
    @ColumnInfo(name = "manufacturers_id")
    private int manufacturers_id;
    @ColumnInfo(name = "manufacturer_name")
    private String manufacturer_name;
    @ColumnInfo(name = "product_taxClassID")
    private int product_taxClassID;
    @ColumnInfo(name = "tax_description")
    private String tax_description;
    @ColumnInfo(name = "tax_class_title")
    private String tax_class_title;
    @ColumnInfo(name = "tax_class_description")
    private String tax_class_description;
    @ColumnInfo(name = "is_sale_product")
    private String is_sale_product;
    @ColumnInfo(name = "cart_date_added")
    private String cart_date_added;

    public ec_user_cart(int cart_id, int products_id, String products_name, String products_image, String products_url, String product_model, String products_weight, String products_weight_unit, int product_stock, int product_quantity, String product_price, String product_attr_price, String product_total_price, String product_final_price, String products_description, String categories_id, String categories_name, int manufacturers_id, String manufacturer_name, int product_taxClassID, String tax_description, String tax_class_title, String tax_class_description, String is_sale_product, String cart_date_added) {
        this.cart_id = cart_id;
        this.products_id = products_id;
        this.products_name = products_name;
        this.products_image = products_image;
        this.products_url = products_url;
        this.product_model = product_model;
        this.products_weight = products_weight;
        this.products_weight_unit = products_weight_unit;
        this.product_stock = product_stock;
        this.product_quantity = product_quantity;
        this.product_price = product_price;
        this.product_attr_price = product_attr_price;
        this.product_total_price = product_total_price;
        this.product_final_price = product_final_price;
        this.products_description = products_description;
        this.categories_id = categories_id;
        this.categories_name = categories_name;
        this.manufacturers_id = manufacturers_id;
        this.manufacturer_name = manufacturer_name;
        this.product_taxClassID = product_taxClassID;
        this.tax_description = tax_description;
        this.tax_class_title = tax_class_title;
        this.tax_class_description = tax_class_description;
        this.is_sale_product = is_sale_product;
        this.cart_date_added = cart_date_added;
    }

    public int getCart_id() {
        return cart_id;
    }

    public void setCart_id(int cart_id) {
        this.cart_id = cart_id;
    }

    public int getProducts_id() {
        return products_id;
    }

    public void setProducts_id(int products_id) {
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

    public int getProduct_stock() {
        return product_stock;
    }

    public void setProduct_stock(int product_stock) {
        this.product_stock = product_stock;
    }

    public int getProduct_quantity() {
        return product_quantity;
    }

    public void setProduct_quantity(int product_quantity) {
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

    public int getManufacturers_id() {
        return manufacturers_id;
    }

    public void setManufacturers_id(int manufacturers_id) {
        this.manufacturers_id = manufacturers_id;
    }

    public String getManufacturer_name() {
        return manufacturer_name;
    }

    public void setManufacturer_name(String manufacturer_name) {
        this.manufacturer_name = manufacturer_name;
    }

    public int getProduct_taxClassID() {
        return product_taxClassID;
    }

    public void setProduct_taxClassID(int product_taxClassID) {
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
}
