package com.example.travelwise.models;

public class Driver {

    private String name;
    private String licenseNumber;

    // Constructor
    public Driver(String name, String licenseNumber) {
        this.name = name;
        this.licenseNumber = licenseNumber;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    // Optional: Override toString for easy debugging
    @Override
    public String toString() {
        return "Driver{" +
                ", name='" + name + '\'' +
                ", licenseNumber='" + licenseNumber + '\'' +
                '}';
    }
}