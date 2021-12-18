package com.trashparadise.lifemanager.bean;

public class PackLoginBean {
    private String username;
    private String uuid;

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

    public PackLoginBean(String username, String uuid) {
        this.username = username;
        this.uuid = uuid;
    }
}
