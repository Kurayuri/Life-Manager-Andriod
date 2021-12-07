package com.trashparadise.lifemanager;

import android.app.Application;
import android.util.Log;

import com.trashparadise.lifemanager.constants.TypeRes;

import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;
import java.util.TreeSet;

public class LifeManagerApplication extends Application {
    private TreeSet<Bill> billList;

    @Override

    public void onCreate() {
        super.onCreate();
        readDate();
    }

    public void readDate() {
        ObjectInput in;
        Log.e("User Date Operation", "Read");
        try {
            in = new ObjectInputStream(openFileInput("billList.data"));
            billList = (TreeSet<Bill>) in.readObject();
            in.close();
            Log.e("billList.data", billList.size() + "");
        } catch (Exception e) {
            billList = new TreeSet<>();
            Log.e("Read Error", e.toString());
        }
    }

    public void saveDate() {
        ObjectOutput out;
        Log.e("User Date Operation", "Write");
        try {
            out = new ObjectOutputStream(openFileOutput("billList.data", MODE_PRIVATE));
            out.writeObject(billList);
            Log.e("billList.data", billList.size() + "");
            out.close();
        } catch (Exception e) {
            Log.e("Write Error", e.toString());
        }
    }

    public void delBill(String uuid) {
        Bill bill = getBill(uuid);
        if (bill != null)
            billList.remove(bill);
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

    public ArrayList<Bill> getBillList(Date dateStart, Date dateEnd, Integer form) {
        ArrayList<Bill> billFiltered = new ArrayList<>();
        for (Bill bill : billList) {
            if (bill.getDate().compareTo(dateStart) >= 0 && bill.getDate().compareTo(dateEnd) <= 0 &&
                    (bill.getForm().equals(form)||form.equals(-1))) {
                billFiltered.add(bill);
            }
        }
                return billFiltered;
    }

    public ArrayList<Bill> getBillList() {
        return new ArrayList<Bill>(billList);
    }

}
