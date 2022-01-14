package com.trashparadise.lifemanager.bean.network;

public class DownloadResponse extends Response {
    public static final int MISS=2;
    private String data;
    public static final String descriptions[] = new String[]{
            "Download Successes",
            "Authentication Fails",
            "User Data Doesn't Exist"
    };

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public DownloadResponse(int state, String data) {
        super(state);
        description=descriptions[state];
        this.data=data;
    }
}
