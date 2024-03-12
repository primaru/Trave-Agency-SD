package com.example.SD.model;

public class Detail {
    private long id;
    private double price;
    private String airline;

    public long getId() {
        return id;
    }

    public double getPrice() {
        return price;
    }

    public String getAirline() {
        return airline;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setAirline(String airline) {
        this.airline = airline;
    }
}
