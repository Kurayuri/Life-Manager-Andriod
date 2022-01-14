package com.trashparadise.lifemanager.bean.network;

public class RegisterRequest {
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public RegisterRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }


}
