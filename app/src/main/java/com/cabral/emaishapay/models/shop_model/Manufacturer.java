package com.cabral.emaishapay.models.shop_model;

public class Manufacturer {
    private Integer manufacturers_id;
    private String manufacturer_name;

    public Manufacturer(Integer manufacturers_id, String manufacturer_name) {
        this.manufacturers_id = manufacturers_id;
        this.manufacturer_name = manufacturer_name;
    }

    public Integer getManufacturers_id() {
        return manufacturers_id;
    }

    public String getManufacturer_name() {
        return manufacturer_name;
    }
}
