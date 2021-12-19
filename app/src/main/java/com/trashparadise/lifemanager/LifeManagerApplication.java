package com.trashparadise.lifemanager;

import android.app.AlertDialog;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.gson.Gson;
import com.trashparadise.lifemanager.bean.Contact;
import com.trashparadise.lifemanager.bean.DataBundleBean;
import com.trashparadise.lifemanager.bean.Bill;
import com.trashparadise.lifemanager.bean.Preference;
import com.trashparadise.lifemanager.bean.User;
import com.trashparadise.lifemanager.bean.Work;
import com.trashparadise.lifemanager.service.MessageGetThread;
import com.trashparadise.lifemanager.service.NotificationThread;
import com.trashparadise.lifemanager.ui.works.WorkEditActivity;
import com.trashparadise.lifemanager.util.RequestUtils;

import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class LifeManagerApplication extends Application {
    private TreeSet<Bill> billList;
    private User user;
    private TreeSet<Work> workList;
    private TreeSet<Work> workListTmp;
    private Preference preference;
    private TreeSet<Contact> contactList;
    private NotificationChannel channel;
    private NotificationManagerCompat notificationManager;
    private NotificationCompat.Builder builder;
    private static final String CHANNEL_ID = "Life Manager";
    private Bitmap icon;

    public String onPush() {
        Gson gson = new Gson();
        DataBundleBean dataBundleBean = new DataBundleBean(billList, workList, contactList, preference);
        String json = gson.toJson(dataBundleBean);
        Log.e("Push", json);
        return json;
    }

    public void onPull(String json) {
        Log.e("Pull", json);
        Gson gson = new Gson();
        DataBundleBean dataBundleBean = gson.fromJson(json, DataBundleBean.class);
        if (dataBundleBean.getBillList() != null)
            billList = dataBundleBean.getBillList();
        if (dataBundleBean.getPreference() != null)
            preference = dataBundleBean.getPreference();
        if (dataBundleBean.getWorkList() != null)
            workList = dataBundleBean.getWorkList();
        if (dataBundleBean.getContactList() != null)
            contactList = dataBundleBean.getContactList();
    }

    public void workSend(String uuid, String tarUUid) {
        Gson gson = new Gson();
        Work work = this.getWork(uuid);
        if (work != null) {
            new Thread(new Runnable() {
                @Override

                public void run() {
                    Log.e("Send", gson.toJson(work));
                    RequestUtils.send(user.getUuid(), tarUUid, gson.toJson(work));
                }
            }).start();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        readDate();
        renewWork();

        // Create notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
        }
        icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.icon_notification);
        notificationManager = NotificationManagerCompat.from(this);
        notificationManager.createNotificationChannel(channel);
        builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        Intent i = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);
        builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_notification)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setLargeIcon(icon)
                .setContentText(getString(R.string.need_todo))
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        workListTmp = new TreeSet<>();
        NotificationThread notificationThread = new NotificationThread(this);
        notificationThread.start();
        MessageGetThread getMessageThread = new MessageGetThread(this);
        getMessageThread.start();
    }

    public void workReceive(Work work) {
        workListTmp.add(work);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Intent intent = new Intent(this, WorkEditActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("tmpUuid", work.getUuid());
        startActivity(intent);
    }

    public void notify(int id, String uuid) {
        Work work = getWork(uuid);
        builder.setContentTitle(work.getTitle());
        Notification notification = builder.build();
        notificationManager.notify(id, notification);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void readDate() {
        ObjectInput in;
        Log.e("User Date Operation", "Read");
        Exception exception = null;
        try {
            in = new ObjectInputStream(openFileInput("billList.data"));
            billList = (TreeSet<Bill>) in.readObject();
            in.close();
            Log.e("billList.data", billList.size() + "");
        } catch (Exception e) {
            billList = new TreeSet<>();
            exception = e;
        }
        try {
            in = new ObjectInputStream(openFileInput("workList.data"));
            workList = (TreeSet<Work>) in.readObject();
            in.close();
            Log.e("workList.data", workList.size() + "");
        } catch (Exception e) {
            workList = new TreeSet<>();
            exception = e;
        }
        try {

            in = new ObjectInputStream(openFileInput("preference.data"));
            preference = (Preference) in.readObject();
            in.close();
        } catch (Exception e) {
            preference = new Preference();
            exception = e;
        }
        try {
            in = new ObjectInputStream(openFileInput("user.data"));
            user = (User) in.readObject();
            in.close();
        } catch (Exception e) {
            user = new User();
            exception = e;
        }
        try {
            in = new ObjectInputStream(openFileInput("contactList.data"));
            contactList = (TreeSet<Contact>) in.readObject();
            in.close();
            Log.e("contactList.data", contactList.size() + "");
        } catch (Exception e) {
            contactList = new TreeSet<>();
            exception = e;

        }
        if (exception != null) {
            Log.e("Read Error", exception.toString());
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

            out = new ObjectOutputStream(openFileOutput("user.data", MODE_PRIVATE));
            out.writeObject(user);
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

}
