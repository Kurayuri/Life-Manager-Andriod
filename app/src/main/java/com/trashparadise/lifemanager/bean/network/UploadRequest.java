package com.trashparadise.lifemanager.bean.network;

public class UploadRequest extends SessionRequest {
    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public UploadRequest(String uuid, String session, String data) {
        super(uuid, session);
        this.data = data;
    }
}
