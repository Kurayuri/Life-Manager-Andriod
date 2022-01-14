package com.trashparadise.lifemanager.bean.network;

public class BaseRequest {
    private String uuid;
    private String session;

    public BaseRequest(String username, String session) {
        this.uuid = username;
        this.session = session;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }
}
