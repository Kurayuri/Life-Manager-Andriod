package com.trashparadise.lifemanager.bean.network;

public class SendResponse extends Response{
    public static final int MISS=2;
    public static final String descriptions[] = new String[]{
            "Send Successes",
            "Authentication Fails",
            "Target User Doesn't Exist"
    };

    public SendResponse(int state) {
        super(state);
        description=descriptions[state];
    }
}
