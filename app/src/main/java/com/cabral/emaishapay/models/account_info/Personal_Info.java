package com.cabral.emaishapay.models.account_info;

public class Personal_Info {
    private String user_id;
    private String dob;
    private String gender;
    private String next_of_kin;
    private String next_of_kin_contact;
    private String user_photo;

    public Personal_Info(String user_id, String dob, String gender, String next_of_kin, String next_of_kin_contact, String user_photo) {
        this.user_id = user_id;
        this.dob = dob;
        this.gender = gender;
        this.next_of_kin = next_of_kin;
        this.next_of_kin_contact = next_of_kin_contact;
        this.user_photo = user_photo;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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

    public String getUser_photo() {
        return user_photo;
    }

    public void setUser_photo(String user_photo) {
        this.user_photo = user_photo;
    }
}
