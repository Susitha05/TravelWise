package com.example.travelwise.models;

public class seatnumber {
    private String seatnumber;
    private String username;
    public seatnumber(String seatnumber, String username){
        this.seatnumber =seatnumber;
        this.username = username;
    }
    public String getSeatnumber(){
        return seatnumber;
    }
    public String getUsername(){
        return username;
    }
    public String getresever(){
        String set = "Reserved";
        return set;
    }
}
