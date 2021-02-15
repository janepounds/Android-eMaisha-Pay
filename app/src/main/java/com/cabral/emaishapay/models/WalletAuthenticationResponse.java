package com.cabral.emaishapay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WalletAuthenticationResponse {
    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private ResposeData data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ResposeData getData() {
        return data;
    }

    public void setData(ResposeData data) {
        this.data = data;
    }

    public class ResposeData{
        @SerializedName("sms_results")
        @Expose
        private String sms_results;

        public String getSms_results() {
            return sms_results;
        }

        public void setSms_results(String sms_code) {
            this.sms_results = sms_code;
        }
    }
}
