package com.cabral.emaishapay.models.external_transfer_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BanksInfoResponse {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private com.cabral.emaishapay.models.external_transfer_model.BanksInfoResponse.InfoData data;

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

    public com.cabral.emaishapay.models.external_transfer_model.BanksInfoResponse.InfoData getData() {
        return data;
    }

    public void setData(com.cabral.emaishapay.models.external_transfer_model.BanksInfoResponse.InfoData data) {
        this.data = data;
    }

    public class InfoData{
        @SerializedName("Banks")
        @Expose
        private Bank[] Banks;

        public Bank[] getBanks() {
            return Banks;
        }

        public void setBanks(Bank[] banks) {
            Banks = banks;
        }
    }




}
