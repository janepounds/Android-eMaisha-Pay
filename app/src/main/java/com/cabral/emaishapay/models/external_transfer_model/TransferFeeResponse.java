package com.cabral.emaishapay.models.external_transfer_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransferFeeResponse {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private com.cabral.emaishapay.models.external_transfer_model.TransferFeeResponse.InfoData[] data;

    public class InfoData{
        @SerializedName("currency")
        @Expose
        private String currency;
        @SerializedName("fee_type")
        @Expose
        private String fee_type;
        @SerializedName("fee")
        @Expose
        private double fee;

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getFee_type() {
            return fee_type;
        }

        public void setFee_type(String fee_type) {
            this.fee_type = fee_type;
        }

        public double getFee() {
            return fee;
        }

        public void setFee(double fee) {
            this.fee = fee;
        }
    }

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

    public InfoData[] getData() {
        return data;
    }

    public void setData(InfoData[] data) {
        this.data = data;
    }

    public double calculateFees(double amount) {
        double fee=0;
        InfoData[] feesArray=this.getData();
        if(feesArray.length>1){
            return feesArray[0].getFee()+( feesArray[1].getFee()*amount);
        }

        return fee;
    }
}
