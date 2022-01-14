package com.trashparadise.lifemanager.bean.network;

public class LoginResponse extends Response{
    public static final int MISS=2;
    public static final String descriptions[] = new String[]{
            "Login Successes",
            "Password Error",
            "Username Doesn't Exist"
    };

    public String session;
    public String uuid;
    public String username;

    public LoginResponse(int state,String session,String uuid,String username) {
        super(state);
        description=descriptions[state];
        this.session=session;
        this.uuid=uuid;
        this.username=username;
    }
}
