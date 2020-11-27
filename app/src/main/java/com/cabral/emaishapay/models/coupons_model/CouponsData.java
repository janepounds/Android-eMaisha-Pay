package com.cabral.emaishapay.models.coupons_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CouponsData {
    @SerializedName("success")
    @Expose
    private String success;
    @SerializedName("data")
    @Expose
    private List<CouponsInfo> data = null;
    @SerializedName("message")
    @Expose
    private String message;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public List<CouponsInfo> getData() {
        return data;
    }

    public void setData(List<CouponsInfo> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
