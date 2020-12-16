package com.cabral.emaishapay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CancelLoanResponse {
    @SerializedName("status")
    @Expose
    private int Status;
    @SerializedName("status")
    @Expose
    private String message;

    public int getStatus() {
        return Status;
    }

    public String getMessage() {
        return message;
    }
}
