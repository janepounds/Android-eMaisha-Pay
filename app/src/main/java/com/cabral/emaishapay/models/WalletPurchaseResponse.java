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


        @SerializedName("referenceNumber")
        @Expose
        private String referenceNumber;


        @SerializedName("balance")
        @Expose
        private String balance;

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


        public String getReferenceNumber() {
            return referenceNumber;
        }

        public void setReferenceNumber(String referenceNumber) {
            this.referenceNumber = referenceNumber;
        }

        public String getBalance() {
            return balance;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }
    }



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}


