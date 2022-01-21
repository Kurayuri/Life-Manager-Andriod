package com.trashparadise.lifemanager.bean;

import java.io.Serializable;
import java.util.Calendar;

public class Contact implements Serializable, Comparable<Contact> {
    private String uuid;
    private String remarkName;
    private String contactUuid;
    private Calendar modifiedTime;

    public static final int UUID = 0;
    public static final int REMARKNAME = 1;
    public static final int CONTACTUUID = 2;

    public String getRemarkName() {
        return remarkName;
    }

    public void setRemarkName(String remarkName) {
        this.remarkName = remarkName;
    }

    public String getContactUuid() {
        return contactUuid;
    }

    public void setContactUuid(String contactUuid) {
        this.contactUuid = contactUuid;
    }

    public Calendar getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Calendar modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public void onModify() {
        this.modifiedTime = Calendar.getInstance();
    }

    public Contact(String remarkName, String contactUuid) {
        this.remarkName = remarkName;
        this.contactUuid = contactUuid;
        this.modifiedTime = Calendar.getInstance();
        this.uuid = java.util.UUID.randomUUID().toString().replaceAll("-", "");
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void set(int field, Object object) {
        switch (field) {
            case UUID:
                this.setUuid((String) object);
                break;
            case REMARKNAME:
                this.setRemarkName((String) object);
                break;
            case CONTACTUUID:
                this.setContactUuid((String) object);
                break;

        }
        onModify();
    }


    @Override
    public int compareTo(Contact o) {
        return o.getContactUuid().compareTo(this.getContactUuid());
    }
}
