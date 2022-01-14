package com.trashparadise.lifemanager.bean.network;

public class UploadResponse extends Response{
    public static final int FAIL=2;
    public static final String descriptions[] = new String[]{
            "Upload Successes",
            "Authentication Fails",
            "Upload Fails"
    };

    public UploadResponse(int state) {
        super(state);
        description=descriptions[state];
    }
}

