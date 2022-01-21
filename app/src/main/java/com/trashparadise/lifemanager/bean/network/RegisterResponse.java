package com.trashparadise.lifemanager.bean.network;

public class RegisterResponse extends Response {
    public static final int EXIST=2;
    public static final String[] descriptions = new String[]{
            "Register Successes",
            "Register Error",
            "Username Exists"
    };

    public RegisterResponse(int state) {
        super(state);
        description=descriptions[state];
    }
}
