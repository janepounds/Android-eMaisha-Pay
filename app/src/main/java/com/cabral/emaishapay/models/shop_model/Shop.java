package com.cabral.emaishapay.models.shop_model;

public class Shop {
    private final Integer shop_id;
    private final String Shop_name;
    private final String shop_contact;
    private final String password;
    private final String shop_email;
    private final String shop_address;
    private final String shop_currency;
    private final String latitude;
    private final String longitude;
    private final String created_at;
    private final String updated_at;

    public Shop(Integer shop_id, String shop_name, String shop_contact, String password, String shop_email, String shop_address, String shop_currency, String latitude, String longitude, String created_at, String updated_at) {
        this.shop_id = shop_id;
        Shop_name = shop_name;
        this.shop_contact = shop_contact;
        this.password = password;
        this.shop_email = shop_email;
        this.shop_address = shop_address;
        this.shop_currency = shop_currency;
        this.latitude = latitude;
        this.longitude = longitude;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public Integer getShop_id() {
        return shop_id;
    }

    public String getShop_name() {
        return Shop_name;
    }

    public String getShop_contact() {
        return shop_contact;
    }

    public String getPassword() {
        return password;
    }

    public String getShop_email() {
        return shop_email;
    }

    public String getShop_address() {
        return shop_address;
    }

    public String getShop_currency() {
        return shop_currency;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }
}
