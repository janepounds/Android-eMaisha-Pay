package com.cabral.emaishapay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WalletPurchaseResponse {
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("data")
    @Expose
    private PurchaseData data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public PurchaseData getData() {
        return data;
    }

    public void setData(PurchaseData data) {
        this.data = data;
    }

    public class PurchaseData{
        @SerializedName("status")
        @Expose
        private String status;

        @SerializedName("transactionId")
        @Expose
        private String transactionId;

        @SerializedName("perweek")
        @Expose
        private String perweek;

        @SerializedName("total")
        @Expose
        private String total;

        @SerializedName("level")
        @Expose
        private String level;


        @SerializedName("amount")
        @Expose
        private String amount;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public String getPerweek() {
            return perweek;
        }

        public String getTotal() {
            return total;
        }

        public String getLevel() {
            return level;
        }

        public String getAmount() {
            return amount;
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}


