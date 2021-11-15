package com.example.project_nanlina;

public class PM {
    String name;
    String address;
    String photo;
    String kickboard;
    String bicycle;
    String number;

    public PM(String name, String address, String photo, String kickboard, String bicycle, String number) {
        this.name = name;
        this.address = address;
        this.photo = photo;
        this.kickboard = kickboard;
        this.bicycle = bicycle;
        this.number = number;
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

    public String getKickboard() {
        return kickboard;
    }

    public void setKickboard(String kickboard) {
        this.kickboard = kickboard;
    }

    public String getBicycle() {
        return bicycle;
    }

    public void setBicycle(String bicycle) {
        this.bicycle = bicycle;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
