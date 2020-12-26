package com.cabral.emaishapay.models;

public class WalletPurchase {
    static com.cabral.emaishapay.models.WalletPurchase purchase = new com.cabral.emaishapay.models.WalletPurchase();
    String mechantId, coupon, mobileNumber;
    float amount;

    String cardNumber, account_name, cardExpiry, cvv;

    String methodOfPayment;

    public static com.cabral.emaishapay.models.WalletPurchase getInstance(){
        return purchase;
    }

    public void setMechantId(String mechantId) {
        this.mechantId = mechantId;
    }



    public String getMechantId() {
        return mechantId;
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

    public static WalletPurchase getPurchase() {
        return purchase;
    }

    public static void setPurchase(WalletPurchase purchase) {
        WalletPurchase.purchase = purchase;
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
}
