package com.cabral.emaishapay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BeneficiaryResponse {
    @SerializedName("beneficiaries")
    @Expose
    private List<Beneficiaries> beneficiariesList;

    public List<Beneficiaries> getBeneficiariesList() {
        return beneficiariesList;
    }

    public void setBeneficiariesList(List<Beneficiaries> beneficiariesList) {
        this.beneficiariesList = beneficiariesList;
    }

    public class Beneficiaries {
        @SerializedName("beneficiary_name")
        @Expose
        private String beneficiary_name;
        @SerializedName("initials")
        @Expose
        private String initials;
        @SerializedName("beneficiary_type")
        @Expose
        private String beneficiary_type;
        @SerializedName("beneficiary_number")
        @Expose
        private String beneficiary_number;

        public String getBeneficiary_name() {
            return beneficiary_name;
        }

        public void setBeneficiary_name(String beneficiary_name) {
            this.beneficiary_name = beneficiary_name;
        }

        public String getInitials() {
            return initials;
        }

        public void setInitials(String initials) {
            this.initials = initials;
        }

        public String getBeneficiary_type() {
            return beneficiary_type;
        }

        public void setBeneficiary_type(String beneficiary_type) {
            this.beneficiary_type = beneficiary_type;
        }

        public String getBeneficiary_number() {
            return beneficiary_number;
        }

        public void setBeneficiary_number(String beneficiary_number) {
            this.beneficiary_number = beneficiary_number;
        }
    }
}
