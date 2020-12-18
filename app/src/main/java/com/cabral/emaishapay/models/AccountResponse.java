package com.cabral.emaishapay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AccountResponse {
    @SerializedName("businessInfo")
    @Expose
    public BusinessInfoData businessInfo;

    @SerializedName("employeeInfo")
    @Expose
    public EmploymentInfoData employeeInfo;

    @SerializedName("userIdInfo")
    @Expose
    public IdInfoData userIdInfo;

    @SerializedName("profile")
    @Expose
    public PersonalInfoData profile;

    public BusinessInfoData getBusinessInfo() {
        return businessInfo;
    }

    public void setBusinessInfo(BusinessInfoData businessInfo) {
        this.businessInfo = businessInfo;
    }

    public EmploymentInfoData getEmployeeInfo() {
        return employeeInfo;
    }

    public void setEmployeeInfo(EmploymentInfoData employeeInfo) {
        this.employeeInfo = employeeInfo;
    }

    public IdInfoData getUserIdInfo() {
        return userIdInfo;
    }

    public void setUserIdInfo(IdInfoData userIdInfo) {
        this.userIdInfo = userIdInfo;
    }

    public PersonalInfoData getProfile() {
        return profile;
    }

    public void setProfile(PersonalInfoData profile) {
        this.profile = profile;
    }

    public class BusinessInfoData{
        @SerializedName("business_name")
        @Expose
        public String business_name;
        @SerializedName("business_location")
        @Expose
        public String business_location;
        @SerializedName("license_no")
        @Expose
        public String license_no;
        @SerializedName("trade_license")
        @Expose
        public String trade_license;
        @SerializedName("registration_no")
        @Expose
        public String registration_no;
        @SerializedName("registration_cert")
        @Expose
        public String registration_cert;

        public String getBusiness_name() {
            return business_name;
        }

        public void setBusiness_name(String business_name) {
            this.business_name = business_name;
        }

        public String getBusiness_location() {
            return business_location;
        }

        public void setBusiness_location(String business_location) {
            this.business_location = business_location;
        }

        public String getLicense_no() {
            return license_no;
        }

        public void setLicense_no(String license_no) {
            this.license_no = license_no;
        }

        public String getTrade_license() {
            return trade_license;
        }

        public void setTrade_license(String trade_license) {
            this.trade_license = trade_license;
        }

        public String getRegistration_no() {
            return registration_no;
        }

        public void setRegistration_no(String registration_no) {
            this.registration_no = registration_no;
        }

        public String getRegistration_cert() {
            return registration_cert;
        }

        public void setRegistration_cert(String registration_cert) {
            this.registration_cert = registration_cert;
        }
    }

    public class EmploymentInfoData{
        @SerializedName("employer")
        @Expose
        public String employer;
        @SerializedName("designation")
        @Expose
        public String designation;
        @SerializedName("location")
        @Expose
        public String location;
        @SerializedName("employment_contact")
        @Expose
        public String employment_contact;
        @SerializedName("employee_id")
        @Expose
        public String employee_id;

        public String getEmployer() {
            return employer;
        }

        public void setEmployer(String employer) {
            this.employer = employer;
        }

        public String getDesignation() {
            return designation;
        }

        public void setDesignation(String designation) {
            this.designation = designation;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getEmployment_contact() {
            return employment_contact;
        }

        public void setEmployment_contact(String employment_contact) {
            this.employment_contact = employment_contact;
        }

        public String getEmployee_id() {
            return employee_id;
        }

        public void setEmployee_id(String employee_id) {
            this.employee_id = employee_id;
        }
    }

    public class IdInfoData{
        @SerializedName("id_type")
        @Expose
        public String id_type;
        @SerializedName("id_number")
        @Expose
        public String id_number;
        @SerializedName("expiry_date")
        @Expose
        public String expiry_date;
        @SerializedName("front")
        @Expose
        public String front;
        @SerializedName("back")
        @Expose
        public String back;

        public String getId_type() {
            return id_type;
        }

        public void setId_type(String id_type) {
            this.id_type = id_type;
        }

        public String getId_number() {
            return id_number;
        }

        public void setId_number(String id_number) {
            this.id_number = id_number;
        }

        public String getExpiry_date() {
            return expiry_date;
        }

        public void setExpiry_date(String expiry_date) {
            this.expiry_date = expiry_date;
        }

        public String getFront() {
            return front;
        }

        public void setFront(String front) {
            this.front = front;
        }

        public String getBack() {
            return back;
        }

        public void setBack(String back) {
            this.back = back;
        }
    }
    public class PersonalInfoData{
        @SerializedName("pic")
        @Expose
        public String pic;
        @SerializedName("dob")
        @Expose
        public String dob;
        @SerializedName("gender")
        @Expose
        public String gender;
        @SerializedName("next_of_kin")
        @Expose
        public String next_of_kin;
        @SerializedName("next_of_kin_contact")
        @Expose
        public String next_of_kin_contact;

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public String getDob() {
            return dob;
        }

        public void setDob(String dob) {
            this.dob = dob;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getNext_of_kin() {
            return next_of_kin;
        }

        public void setNext_of_kin(String next_of_kin) {
            this.next_of_kin = next_of_kin;
        }

        public String getNext_of_kin_contact() {
            return next_of_kin_contact;
        }

        public void setNext_of_kin_contact(String next_of_kin_contact) {
            this.next_of_kin_contact = next_of_kin_contact;
        }
    }


}
