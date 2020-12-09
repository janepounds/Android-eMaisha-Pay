package com.cabral.emaishapay.models;

public class TransactionModel {
    String initials;
    String userName;
    String date;
    String amount;

    public TransactionModel(String initials, String userName, String date, String amount) {
        this.initials = initials;
        this.userName = userName;
        this.date = date;
        this.amount = amount;
    }

    public String getInitials() {
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
