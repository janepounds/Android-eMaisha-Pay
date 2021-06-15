package com.cabral.emaishapay.models.shop_model;

public class ShopResponse {
    private final Integer shop_id;
    private final String message;
    private final Shop data;

    public ShopResponse(Integer shop_id, String message, Shop data) {
        this.shop_id = shop_id;
        this.message = message;
        this.data = data;
    }

    public Integer getShop_id() {
        return shop_id;
    }

    public String getMessage() {
        return message;
    }

    public Shop getData() {
        return data;
    }
}
