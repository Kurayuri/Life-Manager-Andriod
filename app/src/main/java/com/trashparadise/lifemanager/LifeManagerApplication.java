package com.trashparadise.lifemanager;

import android.app.Application;
import android.util.Log;

import java.util.ArrayList;

public class LifeManagerApplication extends Application {
    private ArrayList<Bill> billList = new ArrayList<>();

    public void addBill(Bill bill) {
        billList.add(bill);
        Log.e("Bill List Length",(billList.size()+""));
    }

    public Bill getBill(String uuid) {
        if (uuid==null)
            return null;
        for (Bill x : billList) {
            if (x.getUuid() == uuid) {
                return x;
            }
        }
        return null;
    }
}
