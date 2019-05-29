package com.example.model;

import java.io.Serializable;

public class Contact implements Serializable {
    int id;

    private String name, phone;
    byte[] avatar;

    public Contact() {
    }

    public Contact(String name, String phone, byte[] avatar) {
        this.name = name;
        this.phone = phone;
        this.avatar = avatar;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return this.phone;
    }
}
