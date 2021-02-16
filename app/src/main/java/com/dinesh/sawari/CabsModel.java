package com.dinesh.sawari;

public class CabsModel {

    public CabsModel() {
    }

    String name,type,ac;
    String bags,seats,price;

    public CabsModel(String name, String type, String ac, String bags, String seats, String price) {
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

    public String getSeats() {
        return seats;
    }

    public void setSeats(String seats) {
        this.seats = seats;
    }

    public String getBags() {
        return bags;
    }

    public void setBags(String bags) {
        this.bags = bags;
    }

    public String getAc() {
        return ac;
    }

    public void setAc(String ac) {
        this.ac = ac;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
