package com.dinesh.sawari;

public class CabsModel {

    public CabsModel() {
    }

    String name,type,ac;
    long bags,seats,price;

    public CabsModel(String name, String type, String ac, long bags, long seats, long price) {
        this.name = name;
        this.type = type;
        this.ac = ac;
        this.bags = bags;
        this.seats = seats;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getSeats() {
        return seats;
    }

    public void setSeats(long seats) {
        this.seats = seats;
    }

    public long getBags() {
        return bags;
    }

    public void setBags(long bags) {
        this.bags = bags;
    }

    public String getAc() {
        return ac;
    }

    public void setAc(String ac) {
        this.ac = ac;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }
}
