package com.cabral.emaishapay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WalletTransactionSummary {
    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private ResponseData data;

    public class ResponseData {
        @SerializedName("lastDebit")
        @Expose
        private WalletTransactionResponse.TransactionData.Transactions lastDebit;
        @SerializedName("lastCredit")
        @Expose
        private WalletTransactionResponse.TransactionData.Transactions lastCredit;

        public WalletTransactionResponse.TransactionData.Transactions getLastDebit() {
            return lastDebit;
        }

        public void setLastDebit(WalletTransactionResponse.TransactionData.Transactions lastDebit) {
            this.lastDebit = lastDebit;
        }

        public WalletTransactionResponse.TransactionData.Transactions getLastCredit() {
            return lastCredit;
        }

        public void setLastCredit(WalletTransactionResponse.TransactionData.Transactions lastCredit) {
            this.lastCredit = lastCredit;
        }
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ResponseData getData() {
        return data;
    }

    public void setData(ResponseData data) {
        this.data = data;
    }
}






