package com.example.donde.models;

public class UserModel {
    String userName;

    public UserModel() {

    }

    public UserModel(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
