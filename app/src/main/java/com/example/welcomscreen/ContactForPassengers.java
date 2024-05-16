package com.example.welcomscreen;

public class ContactForPassengers {
    private String name;
    private String phoneNumber;

    public ContactForPassengers(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
