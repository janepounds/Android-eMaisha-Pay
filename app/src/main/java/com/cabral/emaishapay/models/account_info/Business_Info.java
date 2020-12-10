package com.cabral.emaishapay.models.account_info;

public class Business_Info {
    private String user_id;
    private String business_name;
    private String business_location;
    private String registration_number;
    private String lease_number;
    private String registration_certificate;
    private String trade_license;

    public Business_Info(String user_id, String business_name, String business_location, String registration_number, String lease_number, String registration_certificate, String trade_license) {
        this.user_id = user_id;
        this.business_name = business_name;
        this.business_location = business_location;
        this.registration_number = registration_number;
        this.lease_number = lease_number;
        this.registration_certificate = registration_certificate;
        this.trade_license = trade_license;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

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

    public String getRegistration_number() {
        return registration_number;
    }

    public void setRegistration_number(String registration_number) {
        this.registration_number = registration_number;
    }

    public String getLease_number() {
        return lease_number;
    }

    public void setLease_number(String lease_number) {
        this.lease_number = lease_number;
    }

    public String getRegistration_certificate() {
        return registration_certificate;
    }

    public void setRegistration_certificate(String registration_certificate) {
        this.registration_certificate = registration_certificate;
    }

    public String getTrade_license() {
        return trade_license;
    }

    public void setTrade_license(String trade_license) {
        this.trade_license = trade_license;
    }
}
