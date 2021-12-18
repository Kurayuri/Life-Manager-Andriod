package com.trashparadise.lifemanager.bean;

import java.io.Serializable;

public class Contact implements Serializable, Comparable<Contact>{
    private String name;
    private String uuid;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Contact(String name, String uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    @Override
    public int compareTo(Contact o) {
        return o.getUuid().compareTo(this.getUuid());
    }
}
