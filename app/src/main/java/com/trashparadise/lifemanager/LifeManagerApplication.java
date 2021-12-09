package com.trashparadise.lifemanager;

import android.app.Application;
import android.util.Log;

import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.TreeSet;

public class LifeManagerApplication extends Application {
    private TreeSet<Bill> billList;
    private TreeSet<Work> workList;
    private Preference preference;
    private TreeSet<Contact> contactList;

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

            in = new ObjectInputStream(openFileInput("workList.data"));
            workList = (TreeSet<Work>) in.readObject();
            in.close();
            Log.e("workList.data", workList.size() + "");

            in = new ObjectInputStream(openFileInput("preference.data"));
            preference = (Preference) in.readObject();
            in.close();

            in = new ObjectInputStream(openFileInput("contactList.data"));
            contactList = (TreeSet<Contact>) in.readObject();
            in.close();
            Log.e("contactList.data", contactList.size() + "");
        } catch (Exception e) {
            billList = new TreeSet<>();
            workList = new TreeSet<>();
            preference=new Preference();
            contactList=new TreeSet<>();
            Log.e("Read Error", e.toString());
        }
    }

    public void saveData() {
        ObjectOutput out;
        Log.e("User Date Operation", "Write");
        try {
            out = new ObjectOutputStream(openFileOutput("billList.data", MODE_PRIVATE));
            out.writeObject(billList);
            Log.e("billList.data", billList.size() + "");
            out.close();

            out = new ObjectOutputStream(openFileOutput("workList.data", MODE_PRIVATE));
            out.writeObject(workList);
            Log.e("workList.data", workList.size() + "");
            out.close();

            out = new ObjectOutputStream(openFileOutput("preference.data", MODE_PRIVATE));
            out.writeObject(preference);
            out.close();

            out = new ObjectOutputStream(openFileOutput("contactList.data", MODE_PRIVATE));
            out.writeObject(contactList);
            Log.e("contactList.data", contactList.size() + "");
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

    public void setBill(String uuid, Bill billNew) {
        delBill(uuid);
        addBill(billNew);
    }

    public ArrayList<Bill> getBillList(Calendar dateStart, Calendar dateEnd, Integer form) {
        ArrayList<Bill> billFiltered = new ArrayList<>();
        for (Bill bill : billList) {
            if (bill.getDate().compareTo(dateStart) >= 0 && bill.getDate().compareTo(dateEnd) < 0 &&
                    (bill.getForm().equals(form) || form.equals(-1))) {
                billFiltered.add(bill);
            }
        }
        return billFiltered;
    }

    public ArrayList<Bill> getBillList() {
        return new ArrayList<Bill>(billList);
    }

    public void delWork(String uuid) {
        Work work = getWork(uuid);
        if (work != null)
            workList.remove(work);
    }



    // auto unfold
    public void addWork(Work work) {
        Integer repeat = work.getRepeat();
        String classUuid = work.getClassUuid();
        Integer unfoldTime = 1;
        Integer addFeild = Calendar.SECOND;
        Calendar date = (Calendar) work.getDate().clone();
        Work workNew;

        switch (repeat) {
            case Work.EVERY_DAY:
                unfoldTime = 14;
                addFeild = Calendar.DATE;
                break;
            case Work.EVERY_WEEK:
                unfoldTime = 8;
                addFeild = Calendar.WEEK_OF_MONTH;
                break;
            case Work.EVERY_MONTH:
                unfoldTime = 6;
                addFeild = Calendar.MONTH;
                break;
            case Work.EVERY_YEAR:
                unfoldTime = 2;
                addFeild = Calendar.YEAR;
                break;
        }
        workList.add(work);

        for (int i = 2; i <= unfoldTime; ++i) {
            workNew = work.clone();
            date.add(addFeild, 1);
            workNew.setDate((Calendar) date.clone());
            workList.add(workNew);
        }
    }

    public void addWorkChain(Work work) {
        addWork(work);
    }

    public Work getWork(String uuid) {
        if (uuid == null || uuid.equals(""))
            return null;
        for (Work work : workList) {
            if (work.getUuid().equals(uuid)) {
                return work;
            }
        }
        return null;
    }

    public void setWorkChain(String uuid, Work workNew) {
        delWorkChain(uuid);
        addWork(workNew);
    }

    public void delWorkChain(String uuid) {
        Work work = getWork(uuid);
        Calendar calendar = (Calendar) work.getDate().clone();
        String classUuid = work.getClassUuid();
        Integer form = work.getForm();

        if (work != null)
            workList.remove(work);

        Iterator i = workList.iterator();
        Work x;
        while (i.hasNext()) {
            x = (Work) i.next();
            // Done delete what has done before，to_do delete what to do in the future
            if (x.getClassUuid().equals(classUuid) &&
                    x.getForm().equals(form) && ((
                    (x.getDate().compareTo(calendar) < 0 ? 1 : 0) + (form.equals(0) ? 1 : 0)) == 1 ? true : false)
            ) {
                i.remove();
            }
        }
    }


    public void setWork(String uuid, Work workNew) {
        workNew.setClassUuid(getWork(uuid).getClassUuid());
        delWork(uuid);
        workList.add(workNew);
    }
    public void setWork(String uuid, int field,Object object) {
        Work work=getWork(uuid);
        if (work!=null){
            work.set(field,object);
        }
    }

    public ArrayList<Work> getWorkList(Calendar dateStart, Calendar dateEnd, Integer form) {
        ArrayList<Work> workFiltered = new ArrayList<>();
        for (Work work : workList) {
            if (work.getDate().compareTo(dateStart) >= 0 && work.getDate().compareTo(dateEnd) < 0 &&
                    (work.getForm().equals(form) || form.equals(-1))) {
                workFiltered.add(work);
            }
        }
        return workFiltered;
    }

    public ArrayList<Work> getWorkList() {
        return new ArrayList<Work>(workList);
    }

    public Preference getPreference() {
        return preference;
    }


    public void delContact(String uuid) {
        Contact contact = getContact(uuid);
        if (contact != null)
            contactList.remove(contact);
    }

    public void addContact(Contact contact) {
        contactList.add(contact);
    }

    public Contact getContact(String uuid) {
        if (uuid == null || uuid.equals(""))
            return null;
        for (Contact contact : contactList) {
            if (contact.getUuid().equals(uuid)) {
                return contact;
            }
        }
        return null;
    }

    public void setContact(String uuid, Contact contactNew) {
        delContact(uuid);
        addContact(contactNew);
    }

    public ArrayList<Contact> getContactList() {
        return new ArrayList<Contact>(contactList);
    }

}
