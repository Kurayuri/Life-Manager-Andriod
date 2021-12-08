package com.trashparadise.lifemanager;

import java.io.Serializable;

public class Preference implements  Serializable {
    public static final int HOME_BILL=0;
    public static final int HOME_WORK=1;

    private int home;

    public int getHome() {
        return home;
    }

    public void setHome(int home) {
        this.home = home;
    }

    public Preference(){
        home=HOME_BILL;
    }
}
