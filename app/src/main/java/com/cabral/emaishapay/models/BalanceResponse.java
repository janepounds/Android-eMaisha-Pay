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
        @SerializedName("commission")
        @Expose
        private double commission;
        @SerializedName("TotalBalance")
        @Expose
        private double TotalBalance;

        public double getBalance() {
            return balance;
        }

        public void setBalance(double balance) {
            this.balance = balance;
        }

        public double getCommission() {
            return commission;
        }

        public void setCommission(double commission) {
            this.commission = commission;
        }

        public double getTotalBalance() {
            return TotalBalance;
        }

        public void setTotalBalance(double totalBalance) {
            TotalBalance = totalBalance;
        }
    }

}
