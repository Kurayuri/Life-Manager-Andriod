package com.trashparadise.lifemanager.bean.network;

import java.util.ArrayList;

public class ReceiveResponse extends Response {
    public static final int UNKNOWN = 2;
    private String data;
    public static final String[] descriptions = new String[]{
            "Receive Successes",
            "Authentication Fails",
            "Unknown Error"
    };

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public ReceiveResponse(int state, String data) {
        super(state);
        description=descriptions[state];
        this.data = data;
    }
}
