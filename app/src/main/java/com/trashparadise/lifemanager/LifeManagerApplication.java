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
import com.trashparadise.lifemanager.bean.Bill;
import com.trashparadise.lifemanager.bean.DataBundleBean;
import com.trashparadise.lifemanager.bean.Preference;
import com.trashparadise.lifemanager.bean.User;
import com.trashparadise.lifemanager.bean.Work;
import com.trashparadise.lifemanager.service.MessageGetThread;
import com.trashparadise.lifemanager.service.NotificationThread;
import com.trashparadise.lifemanager.ui.MainActivity;
import com.trashparadise.lifemanager.ui.works.WorkEditActivity;
import com.trashparadise.lifemanager.util.RequestUtils;

import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.TreeSet;

public class LifeManagerApplication extends Application {
    private DataManager dataManager;
    private NotificationChannel channel;
    private NotificationManagerCompat notificationManager;
    private NotificationCompat.Builder builder;
    private static final String CHANNEL_ID = "Life Manager";
    private Bitmap icon;

    public String onPush() {
        Gson gson = new Gson();
        DataBundleBean dataBundleBean = new DataBundleBean(new TreeSet<>(dataManager.getBillList()), new TreeSet<>(dataManager.getWorkList()),
                new TreeSet<>(dataManager.getContactList()), dataManager.getPreference());
        String json = gson.toJson(dataBundleBean);
        Log.e("Push", json);
        return json;
    }

    public void onPull(String json) {
        Log.e("Pull", json);
        Gson gson = new Gson();
        DataBundleBean dataBundleBean = gson.fromJson(json, DataBundleBean.class);
        if (dataBundleBean.getBillList() != null)
            dataManager.setBillList(dataBundleBean.getBillList());
        if (dataBundleBean.getPreference() != null)
            dataManager.setPreference(dataBundleBean.getPreference());
        if (dataBundleBean.getWorkList() != null)
            dataManager.setWorkList(dataBundleBean.getWorkList());
        if (dataBundleBean.getContactList() != null)
            dataManager.setContactList(dataBundleBean.getContactList());
    }

    public void workSend(String uuid, String tarUUid) {
        Gson gson = new Gson();
        Work work = dataManager.getWork(uuid);
        if (work != null) {
            new Thread(new Runnable() {
                @Override

                public void run() {
                    Log.e("Send", gson.toJson(work));
                    RequestUtils.send(dataManager.getUser().getUuid(), tarUUid, gson.toJson(work));
                }
            }).start();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dataManager = DataManager.getInstance();
        readDate();
        dataManager.renewWork();

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

        NotificationThread notificationThread = new NotificationThread(this);
        notificationThread.start();
        MessageGetThread getMessageThread = new MessageGetThread(this);
        getMessageThread.start();
    }

    public void workReceive(Work work) {
        dataManager.addWorkTmp(work);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Intent intent = new Intent(this, WorkEditActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("tmpUuid", work.getUuid());
        startActivity(intent);
    }

    public void notify(int id, String uuid) {
        Work work = dataManager.getWork(uuid);
        builder.setContentTitle(work.getTitle());
        Notification notification = builder.build();
        notificationManager.notify(id, notification);
    }

    public void readDate() {
        ObjectInput in;
        Log.e("User Date Operation", "Read");
        Exception exception = null;
        try {
            in = new ObjectInputStream(openFileInput("billList.data"));
            dataManager.setBillList((TreeSet<Bill>) in.readObject());
            in.close();
            Log.e("billList.data", dataManager.getBillList().size() + "");
        } catch (Exception e) {
            dataManager.setBillList(new TreeSet<>());
            exception = e;
        }
        try {
            in = new ObjectInputStream(openFileInput("workList.data"));
            dataManager.setWorkList((TreeSet<Work>) in.readObject());
            in.close();
            Log.e("workList.data", dataManager.getWorkList().size() + "");
        } catch (Exception e) {
            dataManager.setWorkList(new TreeSet<>());
            exception = e;
        }
        try {

            in = new ObjectInputStream(openFileInput("preference.data"));
            dataManager.setPreference((Preference) in.readObject());
            in.close();
        } catch (Exception e) {
            dataManager.setPreference(new Preference());
            exception = e;
        }
        try {
            in = new ObjectInputStream(openFileInput("user.data"));
            dataManager.setUser((User) in.readObject());
            in.close();
        } catch (Exception e) {
            dataManager.setUser(new User());
            exception = e;
        }
        try {
            in = new ObjectInputStream(openFileInput("contactList.data"));
            dataManager.setContactList((TreeSet<Contact>) in.readObject());
            in.close();
            Log.e("contactList.data", dataManager.getContactList().size() + "");
        } catch (Exception e) {
            dataManager.setContactList(new TreeSet<>());
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
            out.writeObject(dataManager.getBillList());
            Log.e("billList.data", dataManager.getBillList().size() + "");
            out.close();

            out = new ObjectOutputStream(openFileOutput("workList.data", MODE_PRIVATE));
            out.writeObject(dataManager.getWorkList());
            Log.e("workList.data", dataManager.getWorkList().size() + "");
            out.close();

            out = new ObjectOutputStream(openFileOutput("preference.data", MODE_PRIVATE));
            out.writeObject(dataManager.getPreference());
            out.close();

            out = new ObjectOutputStream(openFileOutput("user.data", MODE_PRIVATE));
            out.writeObject(dataManager.getUser());
            out.close();

            out = new ObjectOutputStream(openFileOutput("contactList.data", MODE_PRIVATE));
            out.writeObject(dataManager.getContactList());
            Log.e("contactList.data", dataManager.getContactList().size() + "");
            out.close();
        } catch (Exception e) {
            Log.e("Write Error", e.toString());
        }
    }
}
