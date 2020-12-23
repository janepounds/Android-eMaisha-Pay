package com.cabral.emaishapay.models.external_transfer_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SettlementResponse {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private com.cabral.emaishapay.models.external_transfer_model.SettlementResponse.InfoData data;

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

    public com.cabral.emaishapay.models.external_transfer_model.SettlementResponse.InfoData getData() {
        return data;
    }

    public void setData(com.cabral.emaishapay.models.external_transfer_model.SettlementResponse.InfoData data) {
        this.data = data;
    }

    public class InfoData{
        @SerializedName("transaction_id")
        @Expose
        private String transaction_id;
        @SerializedName("third_party_fee")
        @Expose
        private String third_party_fee;
        @SerializedName("destination_type")
        @Expose
        private String destination_type;
        @SerializedName("destination_account_no")
        @Expose
        private String destination_account_no;
        @SerializedName("destination_name")
        @Expose
        private String destination_name;
        @SerializedName("beneficiary_name")
        @Expose
        private String beneficiary_name;

        public String getTransaction_id() {
            return transaction_id;
        }

        public void setTransaction_id(String transaction_id) {
            this.transaction_id = transaction_id;
        }

        public String getThird_party_fee() {
            return third_party_fee;
        }

        public void setThird_party_fee(String third_party_fee) {
            this.third_party_fee = third_party_fee;
        }

        public String getDestination_type() {
            return destination_type;
        }

        public void setDestination_type(String destination_type) {
            this.destination_type = destination_type;
        }

        public String getDestination_account_no() {
            return destination_account_no;
        }

        public void setDestination_account_no(String destination_account_no) {
            this.destination_account_no = destination_account_no;
        }

        public String getDestination_name() {
            return destination_name;
        }

        public void setDestination_name(String destination_name) {
            this.destination_name = destination_name;
        }

        public String getBeneficiary_name() {
            return beneficiary_name;
        }

        public void setBeneficiary_name(String beneficiary_name) {
            this.beneficiary_name = beneficiary_name;
        }
    }




}
