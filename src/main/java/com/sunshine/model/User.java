package com.sunshine.model;

public class User {
    private String id;
    private String email;
    private String userName;
    private String password;

    public User() {}

    public User(String id, String email, String userName) {
        this.id = id;
        this.email = email;
        this.userName = userName;
        this.password = "not required";
    }

    public User(String id, String email, String userName,String password) {
        this.id = id;
        this.email = email;
        this.userName = userName;
        this.password = password;
    }


    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return "no password required";
    }
}
