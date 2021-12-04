package com.trashparadise.lifemanager;

import android.app.Application;
import android.util.Log;

import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeSet;

public class LifeManagerApplication extends Application {
    private TreeSet<Bill> billList;

    @Override

    public void onCreate() {
        super.onCreate();
        readDate();
    }

    public void readDate(){
        ObjectInput in;
        Log.e("User Date Operation", "Read");
        try {
            in = new ObjectInputStream(openFileInput("billList.data"));
            billList=(TreeSet<Bill>) in.readObject();
            in.close();
            Log.e("billList.data",billList.size()+"");
        } catch (Exception e) {
            billList=new TreeSet<>();
            Log.e("Read Error", e.toString());
        }
    }

    public void saveDate(){
        ObjectOutput out;
        Log.e("User Date Operation", "Write");
        try {
            out = new ObjectOutputStream(openFileOutput("billList.data", MODE_PRIVATE));
            out.writeObject(billList);
            Log.e("billList.data",billList.size()+"");
            out.close();
        } catch (Exception e) {
            Log.e("Write Error", e.toString());
        }
    }

    public void addBill(Bill bill) {
        billList.add(bill);
    }

    public Bill getBill(String uuid) {
        if (uuid == null || uuid.equals(""))
            return null;
        for (Bill bill : billList) {
            if (bill.getUuid().equals(uuid)) {
                return bill;
            }
        }
        return null;
    }

    public void setBill(Bill bill, Bill billNew) {
        billList.remove(bill);
        billList.add(billNew);
    }

    public ArrayList<Bill> getBillList() {
        return new ArrayList<Bill>(billList);
    }
}
