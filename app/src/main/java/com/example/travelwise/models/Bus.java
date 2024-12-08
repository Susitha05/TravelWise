package com.example.travelwise.models;

public class Bus {
    private String busLicense;
    private String busModel;
    private String busType;
    private int seatingCapacity;
    private String routeFrom;
    private String routeTo;
    private String arrivalTime;
    private String departureTime;

    public Bus(String busLicense, String busModel, String busType, String routeFrom, String routeTo) {
        this.busLicense = busLicense;
        this.busModel = busModel;
        this.busType = busType;
        this.seatingCapacity = seatingCapacity;
        this.routeFrom = routeFrom;
        this.routeTo = routeTo;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
    }

    public String getBusLicense() {
        return busLicense;
    }

    public String getBusModel() {
        return busModel;
    }

    public String getBusType() {
        return busType;
    }

    public int getSeatingCapacity() {
        return seatingCapacity;
    }

    public String getRouteFrom() {
        return routeFrom;
    }

    public String getRouteTo() {
        return routeTo;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public String getDepartureTime() {
        return departureTime;
    }
}
