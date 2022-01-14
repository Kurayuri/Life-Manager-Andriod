package com.trashparadise.lifemanager.bean;

import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String uuid;
    private String session;
    private boolean validation;

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public User() {
        this.username = "";
        this.uuid = "";
        this.session="";
        this.validation = false;
    }

    public boolean isValidation() {
        return validation;
    }

    public void setValidation(boolean validation) {
        this.validation = validation;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
