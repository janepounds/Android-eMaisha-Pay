package com.cabral.emaishapay.models;

public class WalletPurchase {
    static com.cabral.emaishapay.models.WalletPurchase purchase = new com.cabral.emaishapay.models.WalletPurchase();
    String mechantId, coupon;
    float amount;

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
}
