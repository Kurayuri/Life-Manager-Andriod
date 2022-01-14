package com.trashparadise.lifemanager.bean.network;

public class RegisterResponse extends Response {
    public static final String descriptions[] = new String[]{
            "Register Successes",
            "Username Exists"
    };

    public RegisterResponse(int state) {
        super(state);
        description=descriptions[state];
    }
}
