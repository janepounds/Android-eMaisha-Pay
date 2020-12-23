package com.cabral.emaishapay.models.external_transfer_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BankBranchInfoResponse {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private InfoData data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public class InfoData{
        @SerializedName("Data")
        @Expose
        private BankBranch[] BankBranches;

        public BankBranch[] getBankBranches() {
            return BankBranches;
        }

        public void setBankBranches(BankBranch[] bankBranches) {
            BankBranches = bankBranches;
        }
    }

    public InfoData getData() {
        return data;
    }

    public void setData(InfoData data) {
        this.data = data;
    }
}
