package com.example.donde.archive;

public class User {
    String username;
    String first_name;
    String last_name;
    String password;

    public User() {

}

    public User(String username, String first_name, String last_name, String password) {
        this.username = username;
        this.first_name = first_name;
        this.last_name = last_name;
        this.password = password;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}