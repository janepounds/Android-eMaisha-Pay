package com.cabral.emaishapay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SecurityQnsResponse {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<SecurityQnsResponse.SecurityQns> security_qnsList;

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

    public List<SecurityQns> getSecurity_qnsList() {
        return security_qnsList;
    }

    public void setSecurity_qnsList(List<SecurityQns> security_qnsList) {
        this.security_qnsList = security_qnsList;
    }

    public class SecurityQns{
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("qn")
        @Expose
        private String security_qn_name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSecurity_qn_name() {
            return security_qn_name;
        }

        public void setSecurity_qn_name(String security_qn_name) {
            this.security_qn_name = security_qn_name;
        }
    }
}
