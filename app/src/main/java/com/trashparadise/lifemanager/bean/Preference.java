package com.trashparadise.lifemanager.bean;

import java.io.Serializable;
import java.util.TreeMap;

public class Preference implements Serializable {
    public static final int HOME_BILL = 0;
    public static final int HOME_WORK = 1;

    private int home;
    private TreeMap<Integer, Integer> unfoldTimes;

    public int getHome() {
        return home;
    }

    public void setHome(int home) {
        this.home = home;
    }

    public TreeMap<Integer, Integer> getUnfoldTimes() {
        return unfoldTimes;
    }

    public void setUnfoldTimes(TreeMap<Integer, Integer> unfoldTimes) {
        this.unfoldTimes = unfoldTimes;
    }

    public Preference() {
        home = HOME_BILL;
        unfoldTimes = new TreeMap<>();
        unfoldTimes.put(Work.EVERY_NONE, 1);
        unfoldTimes.put(Work.EVERY_DAY, 14);
        unfoldTimes.put(Work.EVERY_WEEK, 8);
        unfoldTimes.put(Work.EVERY_MONTH, 6);
        unfoldTimes.put(Work.EVERY_YEAR, 2);
    }
}
