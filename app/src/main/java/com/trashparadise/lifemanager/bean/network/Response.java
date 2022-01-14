package com.trashparadise.lifemanager.bean.network;

public class Response {
    public static final int OK = 0;
    public static final int ERROR = 1;
    public String description;
    public int state;

    public Response(int state) {
        this.state = state;
    }

}
