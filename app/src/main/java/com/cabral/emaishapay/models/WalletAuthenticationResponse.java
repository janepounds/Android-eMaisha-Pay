package com.cabral.emaishapay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WalletAuthenticationResponse {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private ResposeData data;

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

    public ResposeData getData() {
        return data;
    }

    public void setData(ResposeData data) {
        this.data = data;
    }

    public class ResposeData{
        @SerializedName("sms_code")
        @Expose
        private String sms_code;

        public String getSms_code() {
            return sms_code;
        }

        public void setSms_code(String sms_code) {
            this.sms_code = sms_code;
        }
    }
}
