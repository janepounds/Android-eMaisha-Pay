package com.cabral.emaishapay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BeneficiaryResponse {
    @SerializedName("beneficiaries")
    @Expose
    private List<Beneficiaries> beneficiariesList;

    public List<Beneficiaries> getBeneficiariesList() {
        return beneficiariesList;
    }

    public void setBeneficiariesList(List<Beneficiaries> beneficiariesList) {
        this.beneficiariesList = beneficiariesList;
    }

    public class Beneficiaries {

    }
}
