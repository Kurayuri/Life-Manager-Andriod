package com.trashparadise.lifemanager;

import android.app.Application;
import android.util.Log;

import com.google.gson.Gson;
import com.trashparadise.lifemanager.bean.Bill;
import com.trashparadise.lifemanager.bean.Contact;
import com.trashparadise.lifemanager.bean.DataBundleBean;
import com.trashparadise.lifemanager.bean.Preference;
import com.trashparadise.lifemanager.bean.User;
import com.trashparadise.lifemanager.bean.Work;
import com.trashparadise.lifemanager.bean.network.DownloadResponse;
import com.trashparadise.lifemanager.bean.network.UploadRequest;
import com.trashparadise.lifemanager.service.RequestService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataManager {
    private static DataManager dataManager = new DataManager();
    private User user;
    private Preference preference;
    private TreeSet<Bill> billList;
    private TreeSet<Work> workList;
    private TreeSet<Contact> contactList;
    private TreeSet<Work> workListTmp;
    private TreeSet<String> deletedList;

    private LifeManagerApplication application;

    private DataManager() {
        workListTmp = new TreeSet<>();
        application = null;
    }

    public static DataManager getInstance() {
        return dataManager;
    }

    public void setBillList(TreeSet<Bill> billList) {
        this.billList = billList;
    }

    public void setWorkList(TreeSet<Work> workList) {
        this.workList = workList;
    }

    public void setPreference(Preference preference) {
        this.preference = preference;
    }

    public void setContactList(TreeSet<Contact> contactList) {
        this.contactList = contactList;
    }


    public void addBill(Bill bill) {
        billList.add(bill);
        deletedList.remove(bill.getUuid());
    }

    public void delBill(String uuid) {
        Bill bill = getBill(uuid);
        if (bill != null) {
            billList.remove(bill);
            deletedList.add(uuid);
        }
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
        billNew.set(Bill.UUID, uuid);
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

    public ArrayList<Bill> getBillList(Calendar date, Integer form) {
        Calendar dateStart = Calendar.getInstance();
        Calendar dateEnd = Calendar.getInstance();
        dateStart.setTime(date.getTime());
        dateStart.set(Calendar.DAY_OF_MONTH, 1);
        dateStart.set(Calendar.HOUR_OF_DAY, 0);
        dateStart.set(Calendar.MINUTE, 0);
        dateStart.set(Calendar.SECOND, 0);
        dateStart.set(Calendar.MILLISECOND, 0);
        dateEnd.setTime(dateStart.getTime());
        dateEnd.add(Calendar.MONTH, 1);

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
        if (work != null) {
            workList.remove(work);
            deletedList.add(uuid);
        }
    }

    public void addWork(Work work) {
        workList.add(work);
        deletedList.remove(work.getUuid());
    }

    public void addWorkChain(Work work) {
        Integer repeat = work.getRepeat();
        Integer addField = Calendar.SECOND;
        Calendar date = (Calendar) work.getDate().clone();
        String uuidPrefix = work.getUuid().substring(0, 17);
        long uuidSuffix = Long.parseLong(work.getUuid().substring(17, 32), 16);
        Work workNew;

        int unfoldTime = preference.getUnfoldTimes().get(repeat);
        switch (repeat) {
            case Work.EVERY_DAY:
                addField = Calendar.DATE;
                break;
            case Work.EVERY_WEEK:
                addField = Calendar.WEEK_OF_MONTH;
                break;
            case Work.EVERY_MONTH:
                addField = Calendar.MONTH;
                break;
            case Work.EVERY_YEAR:
                addField = Calendar.YEAR;
                break;
        }
        workList.add(work);
        deletedList.remove(work.getUuid());

        for (int i = 2; i <= unfoldTime; ++i) {
            workNew = work.clone();
            date.add(addField, 1);
            uuidSuffix += 1;
            workNew.set(Work.UUID, uuidPrefix + Long.toHexString(uuidSuffix));
            workNew.set(Work.DATE, (Calendar) date.clone());
            workList.add(workNew);
            deletedList.remove(workNew.getUuid());
        }
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

    public Work getWorkTmp(String tmpUuid) {
        Work w = null;
        if (tmpUuid == null || tmpUuid.equals(""))
            return null;
        for (Work work : workListTmp) {
            if (work.getUuid().equals(tmpUuid)) {
                w = work;
                return work;
            }
        }
        if (w != null)
            workListTmp.remove(w);
        return null;
    }

    public void addWorkTmp(Work work) {
        workListTmp.add(work);
    }

    public void setWorkChain(String uuid, Work workNew) {
        Work work = getWork(uuid);
        if (work != null) {
            workNew.set(Work.UUID, uuid);
            workNew.set(Work.CLASSUUID, work.getClassUuid());
        }
        delWorkChain(uuid);
        addWorkChain(workNew);
    }

    public void delWorkChain(String uuid) {
        Work work = getWork(uuid);
        Calendar calendar = (Calendar) work.getDate().clone();
        String classUuid = work.getClassUuid();
        Integer form = work.getForm();

        if (work != null) {
            workList.remove(work);
            deletedList.add(uuid);
        }

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
                deletedList.add(x.getUuid());
            }
        }
    }

    public void renewWork() {
        try {
            TreeMap<String, Work> heads = new TreeMap<>();
            TreeMap<Integer, Integer> unfoldTimes = preference.getUnfoldTimes();
            for (Work work : workList) {
                if (heads.get(work.getClassUuid()) != null) {
                    if (heads.get(work.getClassUuid()).getDate().compareTo(work.getDate()) < 0) {
                        heads.put(work.getClassUuid(), work);
                    }
                } else {
                    heads.put(work.getClassUuid(), work);
                }
            }

            for (Map.Entry<String, Work> entry : heads.entrySet()) {
                Work work = entry.getValue();
                LocalDateTime currDate = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(new Date().getTime()), ZoneId.systemDefault());

                Calendar workDate = (Calendar) work.getDate().clone();
                LocalDateTime headDate = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(workDate.getTimeInMillis()), ZoneId.systemDefault());

                Work workNew;

                long dateDiff = 1;
                Integer addField = Calendar.SECOND;
                String uuidPrefix = work.getUuid().substring(0, 17);
                long uuidSuffix = Long.parseLong(work.getUuid().substring(17, 32), 16);

                switch (work.getRepeat()) {
                    case Work.EVERY_DAY:
                        dateDiff = ChronoUnit.DAYS.between(currDate, headDate);
                        addField = Calendar.DATE;
                        break;
                    case Work.EVERY_WEEK:
                        dateDiff = ChronoUnit.WEEKS.between(currDate, headDate);
                        addField = Calendar.WEEK_OF_MONTH;
                        break;
                    case Work.EVERY_MONTH:
                        dateDiff = ChronoUnit.MONTHS.between(currDate, headDate);
                        addField = Calendar.MONTH;
                        break;
                    case Work.EVERY_YEAR:
                        dateDiff = ChronoUnit.YEARS.between(currDate, headDate);
                        addField = Calendar.YEAR;
                        break;
                }
                for (int i = 1; i < unfoldTimes.get(work.getRepeat()) - dateDiff; ++i) {
                    workNew = work.clone();
                    workDate.add(addField, 1);
                    uuidSuffix += 1;
                    workNew.set(Work.UUID, uuidPrefix + Long.toHexString(uuidSuffix));
                    workNew.set(Work.DATE, (Calendar) workDate.clone());
                    addWork(workNew);
                    deletedList.remove(workNew.getUuid());
                }
            }
        } catch (Exception e) {
            Log.e("Renew Error", e.toString());
        }
    }

    public void setWork(String uuid, Work workNew) {
        Work work = getWork(uuid);
        if (work != null) {
            workNew.set(Work.CLASSUUID, work.getClassUuid());
            workNew.set(Work.UUID, work.getUuid());
        }
        delWork(uuid);
        addWork(workNew);
    }

    public void setWork(String uuid, int field, Object object) {
        Work work = getWork(uuid);
        if (work != null) {
            work.set(field, object);
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


    public void delContact(String uuid) {
        Contact contact = getContact(uuid);
        if (contact != null) {
            contactList.remove(contact);
            deletedList.add(uuid);
        }
    }

    public void addContact(Contact contact) {
        contactList.add(contact);
        deletedList.remove(contact.getUuid());
    }

    public Contact getContact(String uuid) {
        if (uuid == null || uuid.equals(""))
            return null;
        for (Contact contact : contactList) {
            if (contact.getContactUuid().equals(uuid)) {
                return contact;
            }
        }
        return null;
    }

    public void setContact(String uuid, Contact contactNew) {
        delContact(uuid);
        contactNew.set(Contact.UUID, uuid);
        addContact(contactNew);
    }

    public ArrayList<Contact> getContactList() {
        return new ArrayList<Contact>(contactList);
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Preference getPreference() {
        return preference;
    }

    public TreeSet<Bill> getBills() {
        return billList;
    }

    public TreeSet<Work> getWorks() {
        return workList;
    }

    public TreeSet<Contact> getContacts() {
        return contactList;
    }


    public void setDeletedList(TreeSet<String> deletedList) {
        this.deletedList = deletedList;
    }

    public TreeSet<String> getDeletedList() {
        return deletedList;
    }


    public void setApplication(LifeManagerApplication application) {
        this.application = application;
    }



    public String onUpload() {
        Gson gson = new Gson();
        DataBundleBean dataBundleBean = new DataBundleBean(dataManager.getBills(), dataManager.getWorks(),
                dataManager.getContacts(), dataManager.getPreference(), dataManager.getDeletedList());
        String json = gson.toJson(dataBundleBean);
        Log.e("Upload", json);
        return json;
    }

    public void onDownload(String json) {
        Log.e("Download", json);
        Gson gson = new Gson();
        try {
            DataBundleBean dataBundleBean = gson.fromJson(json, DataBundleBean.class);
            if (dataBundleBean.getBillList() != null)
                dataManager.setBillList(dataBundleBean.getBillList());
            if (dataBundleBean.getPreference() != null) {
                boolean autoSync = dataManager.getPreference().isAutoSync();
                dataManager.setPreference(dataBundleBean.getPreference());
                dataManager.getPreference().set(Preference.AUTOSYNC, autoSync);
            }
            if (dataBundleBean.getWorkList() != null)
                dataManager.setWorkList(dataBundleBean.getWorkList());
            if (dataBundleBean.getContactList() != null)
                dataManager.setContactList(dataBundleBean.getContactList());

            if (dataBundleBean.getDeletedList() != null)
                dataManager.setDeletedList(dataBundleBean.getDeletedList());

        } catch (Exception e) {
            Log.e("Download Error", e.toString());
        }
    }


}
