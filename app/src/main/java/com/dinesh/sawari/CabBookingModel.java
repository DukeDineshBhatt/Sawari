package com.dinesh.sawari;

public class CabBookingModel {

    public CabBookingModel() {
    }

    String from,to,price,date,time;

    public CabBookingModel(String from, String to, String price, String date, String time) {
        this.from = from;
        this.to = to;
        this.price = price;
        this.date = date;
        this.time = time;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

