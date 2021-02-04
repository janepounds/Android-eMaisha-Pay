package com.cabral.emaishapay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MerchantInfoResponse {
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private InfoData data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public InfoData getData() {
        return data;
    }

    public void setData(InfoData data) {
        this.data = data;
    }

    public class InfoData{
        @SerializedName("businessName")
        @Expose
        private String businessName;

        @SerializedName("sms_code")
        @Expose
        private String smsCode;

        @SerializedName("message")
        @Expose
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getSmsCode() {
            return smsCode;
        }

        public void setSmsCode(String smsCode) {
            this.smsCode = smsCode;
        }

        public String getBusinessName() {
            return businessName;
        }

        public void setBusinessName(String businessName) {
            this.businessName = businessName;
        }
    }




}
