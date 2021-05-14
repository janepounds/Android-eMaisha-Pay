package com.cabral.emaishapay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransactionStatusResponse {
    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("data")
    @Expose
    private TransData data;

    public int getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public TransData getData() {
        return data;
    }

    public void setData(TransData data) {
        this.data = data;
    }

    public class TransData {
        @SerializedName("amount")
        @Expose
        private double amount;
        @SerializedName("transaction_id")
        @Expose
        private String transaction_id;
        @SerializedName("transaction_status")
        @Expose
        private String transaction_status;

        TransData(){}

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public String getTransaction_id() {
            return transaction_id;
        }

        public void setTransaction_id(String transaction_id) {
            this.transaction_id = transaction_id;
        }

        public String getTransaction_status() {
            return transaction_status;
        }

        public void setTransaction_status(String transaction_status) {
            this.transaction_status = transaction_status;
        }
    }

}
