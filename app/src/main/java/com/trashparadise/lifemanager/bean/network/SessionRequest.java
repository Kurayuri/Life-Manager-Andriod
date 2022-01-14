package com.trashparadise.lifemanager.bean.network;

public class SessionRequest {
    private String uuid;
    private String session;

    public SessionRequest(String uuid, String session) {
        this.uuid = uuid;
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
