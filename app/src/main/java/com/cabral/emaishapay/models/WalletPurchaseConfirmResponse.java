package com.cabral.emaishapay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WalletPurchaseConfirmResponse {
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private ConfirmData data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ConfirmData getData() {
        return data;
    }

    public void setData(ConfirmData data) {
        this.data = data;
    }

    public class ConfirmData{
        @SerializedName("amount")
        @Expose
        private double amount;
        @SerializedName("charge")
        @Expose
        private double charge;

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public double getCharge() {
            return charge;
        }

        public void setCharge(double charge) {
            this.charge = charge;
        }
    }



}
