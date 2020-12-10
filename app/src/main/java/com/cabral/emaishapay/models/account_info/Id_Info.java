package com.cabral.emaishapay.models.account_info;

public class Id_Info {
    private String user_id;
    private String id_type;
    private String id_number;
    private String expiry_date;
    private String front_id_image;
    private String back_id_image;

    public Id_Info(String user_id, String id_type, String id_number, String expiry_date, String front_id_image, String back_id_image) {
        this.user_id = user_id;
        this.id_type = id_type;
        this.id_number = id_number;
        this.expiry_date = expiry_date;
        this.front_id_image = front_id_image;
        this.back_id_image = back_id_image;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

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

    public String getFront_id_image() {
        return front_id_image;
    }

    public void setFront_id_image(String front_id_image) {
        this.front_id_image = front_id_image;
    }

    public String getBack_id_image() {
        return back_id_image;
    }

    public void setBack_id_image(String back_id_image) {
        this.back_id_image = back_id_image;
    }
}
