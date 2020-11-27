package com.cabral.emaishapay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WalletTransactionReceiptResponse {
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private ReceiptData data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ReceiptData getData() {
        return data;
    }

    public void setData(ReceiptData data) {
        this.data = data;
    }

    public class ReceiptData{
        @SerializedName("transaction")
        @Expose
        private TransactionsData transaction;

        public TransactionsData getTransaction() {
            return transaction;
        }

        public void setTransaction(TransactionsData transaction) {
            this.transaction = transaction;
        }

        public class TransactionsData{
            @SerializedName("type")
            @Expose
            private String type;
            @SerializedName("amount")
            @Expose
            private double amount;
            @SerializedName("dateCompleted")
            @Expose
            private String dateCompleted;
            @SerializedName("status")
            @Expose
            private String status;
            @SerializedName("items")
            @Expose
            private String items;
            @SerializedName("referenceNumber")
            @Expose
            private String referenceNumber;
            @SerializedName("date")
            @Expose
            private String date;
            @SerializedName("receiver")
            @Expose
            private String receiver;
            @SerializedName("sender")
            @Expose
            private String sender;
            @SerializedName("receiptNumber")
            @Expose
            private String receiptNumber;


            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public double getAmount() {
                return amount;
            }

            public void setAmount(double amount) {
                this.amount = amount;
            }

            public String getDateCompleted() {
                return dateCompleted;
            }

            public void setDateCompleted(String dateCompleted) {
                this.dateCompleted = dateCompleted;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getItems() {
                return items;
            }

            public void setItems(String items) {
                this.items = items;
            }

            public String getReferenceNumber() {
                return referenceNumber;
            }

            public void setReferenceNumber(String referenceNumber) {
                this.referenceNumber = referenceNumber;
            }

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public String getReceiver() {
                return receiver;
            }

            public void setReceiver(String receiver) {
                this.receiver = receiver;
            }

            public String getSender() {
                return sender;
            }

            public void setSender(String sender) {
                this.sender = sender;
            }

            public String getReceiptNumber() {
                return receiptNumber;
            }

            public void setReceiptNumber(String receiptNumber) {
                this.receiptNumber = receiptNumber;
            }
        }
    }




}
