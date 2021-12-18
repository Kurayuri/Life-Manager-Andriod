package com.trashparadise.lifemanager.bean;

import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String uuid;
    private boolean validation;
    public User(){
        this.username = "";
        this.validation=false;
        this.uuid = "";
    }

    public boolean isValidation() {
        return validation;
    }

    public void setValidation(boolean validation) {
        this.validation = validation;
    }

    public User(String uuid,
                String userName) {
        this.username = userName;
        this.validation=true;
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username =username;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
