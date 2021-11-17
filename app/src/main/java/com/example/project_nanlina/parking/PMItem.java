package com.example.project_nanlina.parking;

public class PMItem {
    String name;
    String address;
    String photo;
    int kickboard;
    int bicycle;
    int number;

    public PMItem(String name, String address, String photo) {
        this.name = name;
        this.address = address;
        this.photo = photo;
//        this.kickboard = kickboard;
//        this.bicycle = bicycle;
//        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

//    public int getKickboard() {
//        return kickboard;
//    }
//
//    public void setKickboard(int kickboard) {
//        this.kickboard = kickboard;
//    }
//
//    public int getBicycle() {
//        return bicycle;
//    }
//
//    public void setBicycle(int bicycle) {
//        this.bicycle = bicycle;
//    }
//
//    public int getNumber() {
//        return number;
//    }
//
//    public void setNumber(int number) {
//        this.number = number;
//    }
}
