package com.cabral.emaishapay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class InitiateTransferResponse {
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private InitiateTransferData data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public InitiateTransferData getData() {
        return data;
    }

    public void setData(InitiateTransferData data) {
        this.data = data;
    }

    public class InitiateTransferData {
        @SerializedName("amount")
        @Expose
        private ArrayList<String> amount;
        @SerializedName("receiverPhoneNumber")
        @Expose
        private ArrayList<String> receiverPhoneNumber;

        public ArrayList<String> getAmount() {
            return amount;
        }

        public void setAmount(ArrayList<String> amount) {
            this.amount = amount;
        }

        public ArrayList<String> getReceiverPhoneNumber() {
            return receiverPhoneNumber;
        }

        public void setReceiverPhoneNumber(ArrayList<String> receiverPhoneNumber) {
            this.receiverPhoneNumber = receiverPhoneNumber;
        }
    }
}
