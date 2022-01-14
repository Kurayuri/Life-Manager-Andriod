package com.trashparadise.lifemanager;

import com.trashparadise.lifemanager.bean.Bill;
import com.trashparadise.lifemanager.bean.Contact;
import com.trashparadise.lifemanager.bean.Preference;
import com.trashparadise.lifemanager.bean.User;
import com.trashparadise.lifemanager.bean.Work;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class DataManager {
    private static DataManager dataManager = new DataManager();
    private TreeSet<Bill> billList;
    private User user;
    private TreeSet<Work> workList;
    private Preference preference;
    private TreeSet<Contact> contactList;
    private TreeSet<Work> workListTmp;

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

    private DataManager() {
        workListTmp = new TreeSet<>();
    }

    public static DataManager getInstance() {
        return dataManager;
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
        if (work != null)
            workList.remove(work);
    }

    // auto unfold
    public void addWork(Work work) {
        Integer repeat = work.getRepeat();
        Integer addField = Calendar.SECOND;
        Calendar date = (Calendar) work.getDate().clone();
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

        for (int i = 2; i <= unfoldTime; ++i) {
            workNew = work.clone();
            date.add(addField, 1);
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

    public void renewWork() {
        TreeMap<String, Work> head = new TreeMap<>();
        Calendar calendar = Calendar.getInstance();
        TreeMap<Integer, Integer> unfoldTimes = preference.getUnfoldTimes();
        for (Work work : workList) {
            if (head.get(work.getClassUuid()) != null) {
                if (head.get(work.getClassUuid()).getDate().compareTo(work.getDate()) < 0) {
                    head.put(work.getClassUuid(), work);
                }
            } else {
                head.put(work.getClassUuid(), work);
            }
        }
        for (Map.Entry<String, Work> entry : head.entrySet()) {
            Work work = entry.getValue();
            Work workNew;
            Calendar date = (Calendar) work.getDate().clone();
            long dateDiff = 1;
            Integer addField = Calendar.SECOND;

            switch (work.getRepeat()) {
                case Work.EVERY_DAY:
                    dateDiff = ChronoUnit.DAYS.between(calendar.toInstant(), work.getDate().toInstant());
                    addField = Calendar.DATE;
                    break;
                case Work.EVERY_WEEK:
                    dateDiff = ChronoUnit.WEEKS.between(calendar.toInstant(), work.getDate().toInstant());
                    addField = Calendar.WEEK_OF_MONTH;
                    break;
                case Work.EVERY_MONTH:
                    dateDiff = ChronoUnit.MONTHS.between(calendar.toInstant(), work.getDate().toInstant());
                    addField = Calendar.MONTH;
                    break;
                case Work.EVERY_YEAR:
                    dateDiff = ChronoUnit.YEARS.between(calendar.toInstant(), work.getDate().toInstant());
                    addField = Calendar.YEAR;
                    break;
            }
            for (int i = 1; i < unfoldTimes.get(work.getRepeat()) - dateDiff; ++i) {
                workNew = work.clone();
                date.add(addField, 1);
                workNew.setDate((Calendar) date.clone());
                workList.add(workNew);
            }
        }
    }

    public void setWork(String uuid, Work workNew) {
        workNew.setClassUuid(getWork(uuid).getClassUuid());
        delWork(uuid);
        workList.add(workNew);
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
}
