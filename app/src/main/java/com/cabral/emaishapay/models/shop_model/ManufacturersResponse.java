package com.cabral.emaishapay.models.shop_model;

import com.cabral.emaishapay.network.db.entities.EcManufacturer;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ManufacturersResponse {
    @SerializedName("manufacturers")
    @Expose
    List<EcManufacturer> manufacturers;

    public ManufacturersResponse(List<EcManufacturer> manufacturers) {
        this.manufacturers = manufacturers;
    }

    public List<EcManufacturer> getManufacturers() {
        return manufacturers;
    }
}
