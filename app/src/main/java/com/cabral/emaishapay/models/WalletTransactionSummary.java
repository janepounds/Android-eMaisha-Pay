package com.cabral.emaishapay.models;

import com.cabral.emaishapay.network.db.entities.UserTransactions;
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
        private UserTransactions lastDebit;
        @SerializedName("lastCredit")
        @Expose
        private UserTransactions lastCredit;

        public UserTransactions getLastDebit() {
            return lastDebit;
        }

        public void setLastDebit(UserTransactions lastDebit) {
            this.lastDebit = lastDebit;
        }

        public UserTransactions getLastCredit() {
            return lastCredit;
        }

        public void setLastCredit(UserTransactions lastCredit) {
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






