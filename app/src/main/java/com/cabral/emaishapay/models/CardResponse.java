package com.cabral.emaishapay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CardResponse {
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("cardData")
    @Expose
    public CardData cardData;

    public CardData getCardData() {
        return cardData;
    }

    public void setCardData(CardData cardData) {
        this.cardData = cardData;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public class CardData{
        @SerializedName("cards")
        @Expose
        private List<Cards> cardsList;

        public List<Cards> getCardsList() {
            return cardsList;
        }

        public void setCardsList(List<Cards> cardsList) {
            this.cardsList = cardsList;
        }

        public class Cards{
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
