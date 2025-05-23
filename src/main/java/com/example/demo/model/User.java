package com.example.demo.model;

public class User {
    private String username;
    private String password;
    private String role;

    public User(String role, String password, String username) {
        this.role = role;
        this.password = password;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
}
