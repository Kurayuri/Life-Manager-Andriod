package com.trashparadise.lifemanager.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.TreeMap;

public class Preference implements Serializable {
    public static final int HOME_BILL = 0;
    public static final int HOME_WORK = 1;

    public static final int HOME = 0;
    public static final int AUTOSYNC = 1;
    public static final int UNFOLDTIMES = 2;


    private int home;
    private boolean autoSync;
    private TreeMap<Integer, Integer> unfoldTimes;
    private Calendar modifiedTime;

    public boolean isAutoSync() {
        return autoSync;
    }

    public void set(int field, Object object) {
        switch (field) {
            case HOME:
                this.setHome((Integer) object);
                break;
            case AUTOSYNC:
                this.setAutoSync((Boolean) object);
                break;
            case UNFOLDTIMES:
                this.setUnfoldTimes((TreeMap<Integer, Integer>) object);
                break;
        }
        onModify();
    }

    public void setAutoSync(boolean autoSync) {
        this.autoSync = autoSync;
    }


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

    public Calendar getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Calendar modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public void onModify() {
        this.modifiedTime = Calendar.getInstance();
    }

    public Preference() {
        home = HOME_BILL;
        autoSync = false;
        unfoldTimes = new TreeMap<>();
        unfoldTimes.put(Work.EVERY_NONE, 1);
        unfoldTimes.put(Work.EVERY_DAY, 14);
        unfoldTimes.put(Work.EVERY_WEEK, 8);
        unfoldTimes.put(Work.EVERY_MONTH, 6);
        unfoldTimes.put(Work.EVERY_YEAR, 2);
        this.modifiedTime = Calendar.getInstance();
    }
}
