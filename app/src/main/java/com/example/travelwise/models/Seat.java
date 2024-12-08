package com.example.travelwise.models;

public class Seat {
    private String date;
    private String location;
    private String busNumber;
    private String seatNumber;
    private String Bookid;

    public Seat(String Bookid,String date, String location, String busNumber, String seatNumber) {
        this.date = date;
        this.location = location;
        this.busNumber = busNumber;
        this.seatNumber = seatNumber;
    }

    // Getters
    public String getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public String getBookid() {
        return Bookid;
    }
}
