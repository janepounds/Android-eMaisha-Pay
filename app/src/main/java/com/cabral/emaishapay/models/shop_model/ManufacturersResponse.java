package com.cabral.emaishapay.models.shop_model;

import java.util.List;

public class ManufacturersResponse {
    List<Manufacturer> manufacturers;

    public ManufacturersResponse(List<Manufacturer> manufacturers) {
        this.manufacturers = manufacturers;
    }

    public List<Manufacturer> getManufacturers() {
        return manufacturers;
    }
}
