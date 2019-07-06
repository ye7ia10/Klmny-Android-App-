package com.example.owner.klmny;

public class contacts {

    private String image,name, status, userID;

    public contacts(String image, String name, String status, String userID) {
        this.image = image;
        this.name = name;
        this.status = status;
        this.userID = userID;
    }
    public contacts(){}

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
