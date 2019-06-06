package com.luciferhacker.letuschat;

public class Users {

    public String name;
    public String image;
    public String status;
    public String thumb_image;

    public Users () {

    }

    public Users(String name, String image, String status, String thumb_image) {
        this.name = name;
        this.image = image;
        this.status = status;
        this.thumb_image = thumb_image;

    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setThumbImage(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public String getThumbImage() {
        return thumb_image;
    }
}
