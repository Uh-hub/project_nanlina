package com.example.project_nanlina.parking;

public class PMItem {

    String id;
    String name;
    String address;
    String latitude;
    String longitude;
    String photo;
    String kickboard;
    String bicycle;

    public PMItem(String id, String name, String address, String latitude, String longitude, String photo, String kickboard, String bicycle) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.photo = photo;
        this.kickboard = kickboard;
        this.bicycle = bicycle;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
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
}
