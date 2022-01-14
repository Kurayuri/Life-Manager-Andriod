package com.trashparadise.lifemanager.bean.network;

import java.util.ArrayList;

public class ReceiveResponse extends Response {
    public static final int UNKNOWN = 2;
    private ArrayList<String> data;
    public static final String descriptions[] = new String[]{
            "Receive Successes",
            "Authentication Fails",
            "Unknown Error"
    };

    public ArrayList<String> getData() {
        return data;
    }

    public void setData(ArrayList<String> data) {
        this.data = data;
    }

    public ReceiveResponse(int state, ArrayList<String> data) {
        super(state);
        description=descriptions[state];
        this.data = data;
    }
}
