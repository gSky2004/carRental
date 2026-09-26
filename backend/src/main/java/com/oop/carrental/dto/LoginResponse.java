package com.oop.carrental.dto;

public class LoginResponse {

    private String token;
    private String fullName;
    private String username;

    public LoginResponse() {

    }

    public LoginResponse(String token, String fullName, String username) {

        this.token = token;
        this.fullName = fullName;
        this.username = username;

    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
