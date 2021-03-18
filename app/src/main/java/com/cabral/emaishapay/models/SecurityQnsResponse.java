package com.cabral.emaishapay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SecurityQnsResponse {
    @SerializedName("security_qns")
    @Expose
    private List<SecurityQnsResponse.SecurityQns> security_qnsList;

    public List<SecurityQns> getSecurity_qnsList() {
        return security_qnsList;
    }

    public void setSecurity_qnsList(List<SecurityQns> security_qnsList) {
        this.security_qnsList = security_qnsList;
    }

    public class SecurityQns{
        @SerializedName("security_qn_name")
        @Expose
        private String security_qn_name;

        public String getSecurity_qn_name() {
            return security_qn_name;
        }

        public void setSecurity_qn_name(String security_qn_name) {
            this.security_qn_name = security_qn_name;
        }
    }
}
