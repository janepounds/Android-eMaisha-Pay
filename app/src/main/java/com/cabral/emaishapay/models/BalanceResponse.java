package com.cabral.emaishapay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BalanceResponse {
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private BalanceData data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BalanceData getData() {
        return data;
    }

    public void setData(BalanceData data) {
        this.data = data;
    }

    public class BalanceData {
        @SerializedName("balance")
        @Expose
        private double balance;
        @SerializedName("credits")
        @Expose
        private double credits;
        @SerializedName("debits")
        @Expose
        private double debits;

        public double getBalance() {
            return balance;
        }

        public void setBalance(double balance) {
            this.balance = balance;
        }

        public double getCredits() {
            return credits;
        }

        public void setCredits(double credits) {
            this.credits = credits;
        }

        public double getDebits() {
            return debits;
        }

        public void setDebits(double debits) {
            this.debits = debits;
        }
    }

}
