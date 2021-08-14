package com.cabral.emaishapay.models;

import com.cabral.emaishapay.network.db.entities.UserTransactions;
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
        private List<UserTransactions> transactions;
        @SerializedName("cashin")
        @Expose
        private double cashin;
        @SerializedName("cashout")
        @Expose
        private double cashout;
        @SerializedName("bank")
        @Expose
        private double bankTotal;
        @SerializedName("mobileMoney")
        @Expose
        private double mobileMoneyTotal;

        public double getBankTotal() {
            return bankTotal;
        }

        public void setBankTotal(double bankTotal) {
            this.bankTotal = bankTotal;
        }

        public double getMobileMoneyTotal() {
            return mobileMoneyTotal;
        }

        public void setMobileMoneyTotal(double mobileMoneyTotal) {
            this.mobileMoneyTotal = mobileMoneyTotal;
        }

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

        public List<UserTransactions> getTransactions() {
            return transactions;
        }

        public void setTransactions(List<UserTransactions> transactions) {
            this.transactions = transactions;
        }

    }
}


