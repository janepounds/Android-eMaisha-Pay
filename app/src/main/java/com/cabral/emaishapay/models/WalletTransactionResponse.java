package com.cabral.emaishapay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WalletTransactionResponse {
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private TransactionData data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public TransactionData getData() {
        return data;
    }

    public void setData(TransactionData data) {
        this.data = data;
    }

    public class TransactionData{
        @SerializedName("transactions")
        @Expose
        private List<Transactions> transactions;
        @SerializedName("cashin")
        @Expose
        private double cashin;
        @SerializedName("cashout")
        @Expose
        private double cashout;

        public double getCashin() {
            return cashin;
        }

        public void setCashin(double cashin) {
            this.cashin = cashin;
        }

        public double getCashout() {
            return cashout;
        }

        public void setCashout(double cashout) {
            this.cashout = cashout;
        }

        public List<Transactions> getTransactions() {
            return transactions;
        }

        public void setTransactions(List<Transactions> transactions) {
            this.transactions = transactions;
        }

        public class Transactions{
            @SerializedName("type")
            @Expose
            private String type;
            @SerializedName("amount")
            @Expose
            private double amount;
            @SerializedName("ft_discount")
            @Expose
            private double discount;
            @SerializedName("charge")
            @Expose
            private double charge;
            @SerializedName("created_at")
            @Expose
            private String dateCompleted;
            @SerializedName("trans_message")
            @Expose
            private String status;
            @SerializedName("referenceNumber")
            @Expose
            private String referenceNumber;
            @SerializedName("phoneNumber")
            @Expose
            private String phoneNumber;
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

            public String getReferenceNumber() {
                return referenceNumber;
            }

            public void setReferenceNumber(String referenceNumber) {
                this.referenceNumber = referenceNumber;
            }

            public String getPhoneNumber() {
                return phoneNumber;
            }

            public void setPhoneNumber(String phoneNumber) {
                this.phoneNumber = phoneNumber;
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


