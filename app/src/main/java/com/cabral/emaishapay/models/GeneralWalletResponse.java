package com.cabral.emaishapay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GeneralWalletResponse {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("message")
    @Expose
    private BeneficiaryResponse.Beneficiaries data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BeneficiaryResponse.Beneficiaries getData() {
        return data;
    }

    public void setData(BeneficiaryResponse.Beneficiaries data) {
        this.data = data;
    }
}
