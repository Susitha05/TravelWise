package com.example.travelwise.models;

public class User {
    private String firstName;
    private String lastName;
    private String phone;
    private String nic;
    private String email;
    private String city;
    private String userType;

    public User(String firstName, String lastName, String phone, String nic, String email, String city, String userType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.nic = nic;
        this.email = email;
        this.city = city;
        this.userType = userType;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }

    public String getNic() {
        return nic;
    }

    public String getEmail() {
        return email;
    }

    public String getCity() {
        return city;
    }

    public String getUserType() {
        return userType;
    }
}
