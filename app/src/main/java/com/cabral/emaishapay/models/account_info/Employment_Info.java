package com.cabral.emaishapay.models.account_info;

public class Employment_Info {
    private String user_id;
    private String employer;
    private String designation;
    private String location;
    private String contact;
    private String employee_id;

    public Employment_Info(String user_id, String employer, String designation, String location, String contact, String employee_id) {
        this.user_id = user_id;
        this.employer = employer;
        this.designation = designation;
        this.location = location;
        this.contact = contact;
        this.employee_id = employee_id;
    }


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

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

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmployee_id() {
        return employee_id;
    }

    public void setEmployee_id(String employee_id) {
        this.employee_id = employee_id;
    }
}
