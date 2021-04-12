package com.cabral.emaishapay.models.shop_model;

import com.cabral.emaishapay.network.db.entities.EcManufacturer;

import java.util.List;

public class ManufacturersResponse {
    List<EcManufacturer> manufacturers;

    public ManufacturersResponse(List<EcManufacturer> manufacturers) {
        this.manufacturers = manufacturers;
    }

    public List<EcManufacturer> getManufacturers() {
        return manufacturers;
    }
}
