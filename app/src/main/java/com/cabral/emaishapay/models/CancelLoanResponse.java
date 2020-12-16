package com.cabral.emaishapay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CancelLoanResponse {
    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("status")
    @Expose
    private String message;

    public void setStatus(int status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
