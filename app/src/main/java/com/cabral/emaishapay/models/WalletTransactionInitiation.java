package com.cabral.emaishapay.models;

public class WalletTransactionInitiation {
    static WalletTransactionInitiation purchase = new WalletTransactionInitiation();
    String mechantId, coupon, mobileNumber, accountNumber;
    float amount;

    String cardNumber, account_name, cardExpiry, cvv;

    String methodOfPayment;
    String bankCode, bankBranch;

    public static WalletTransactionInitiation getInstance(){
        return purchase;
    }

    public void setMechantId(String mechantId) {
        this.mechantId = mechantId;
    }



    public String getMechantId() {
        return mechantId;
    }


    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

    public String getCoupon() { return coupon; }

    public static WalletTransactionInitiation getPurchase() {
        return purchase;
    }

    public static void setPurchase(WalletTransactionInitiation purchase) {
        WalletTransactionInitiation.purchase = purchase;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getAccount_name() {
        return account_name;
    }

    public void setAccount_name(String account_name) {
        this.account_name = account_name;
    }

    public String getCardExpiry() {
        return cardExpiry;
    }

    public void setCardExpiry(String cardExpiry) {
        this.cardExpiry = cardExpiry;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getMethodOfPayment() {
        return methodOfPayment;
    }

    public void setMethodOfPayment(String methodOfPayment) {
        this.methodOfPayment = methodOfPayment;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankBranch() {
        return bankBranch;
    }

    public void setBankBranch(String bankBranch) {
        this.bankBranch = bankBranch;
    }
}
