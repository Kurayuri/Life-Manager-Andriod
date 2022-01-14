package com.trashparadise.lifemanager.bean.network;

public class SendRequest extends SessionRequest{
    private String dst;
    private String data;

    public String getDst() {
        return dst;
    }

    public void setDst(String dst) {
        this.dst = dst;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public SendRequest(String uuid, String session, String dst, String data) {
        super(uuid, session);
        this.dst = dst;
        this.data = data;
    }
}
