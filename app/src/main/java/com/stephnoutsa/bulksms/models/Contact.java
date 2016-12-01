package com.stephnoutsa.bulksms.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by stephnoutsa on 11/30/16.
 */

public class Contact {

    // Private variables
    @SerializedName("id")
    int id;

    @SerializedName("phone")
    String phone;

    @SerializedName("sent")
    String sent;

    // Empty constructor
    public Contact() {

    }

    // Constructor
    public Contact(int id, String phone, String sent) {
        this.id = id;
        this.phone = phone;
        this.sent = sent;
    }

    // Constructor
    public Contact(String phone, String sent) {
        this.phone = phone;
        this.sent = sent;
    }

    // Getter and setter methods
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSent() {
        return sent;
    }

    public void setSent(String sent) {
        this.sent = sent;
    }

}
