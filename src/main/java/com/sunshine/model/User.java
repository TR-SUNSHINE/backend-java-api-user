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

}
