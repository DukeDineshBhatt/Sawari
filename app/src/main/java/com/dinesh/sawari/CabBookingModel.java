package com.dinesh.sawari;

public class CabBookingModel {

    public CabBookingModel() {
    }

    String from,to,price,date,time,ac,name,seats,type,status;

    public CabBookingModel(String from, String to, String price, String date, String time, String ac, String name, String seats, String type,String status) {
        this.from = from;
        this.to = to;
        this.price = price;
        this.date = date;
        this.time = time;
        this.ac = ac;
        this.name = name;
        this.seats = seats;
        this.type = type;
        this.status = status;
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

    public String getAc() {
        return ac;
    }

    public void setAc(String ac) {
        this.ac = ac;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSeats() {
        return seats;
    }

    public void setSeats(String seats) {
        this.seats = seats;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

