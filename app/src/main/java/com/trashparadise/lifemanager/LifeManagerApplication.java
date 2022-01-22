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
import com.trashparadise.lifemanager.bean.Preference;
import com.trashparadise.lifemanager.bean.User;
import com.trashparadise.lifemanager.bean.Work;
import com.trashparadise.lifemanager.bean.network.DownloadResponse;
import com.trashparadise.lifemanager.bean.network.UploadRequest;
import com.trashparadise.lifemanager.constants.NetworkDescriptionRes;
import com.trashparadise.lifemanager.service.MessageReceiveThread;
import com.trashparadise.lifemanager.service.NotificationThread;
import com.trashparadise.lifemanager.service.RequestService;
import com.trashparadise.lifemanager.ui.MainActivity;
import com.trashparadise.lifemanager.ui.works.WorkEditActivity;

import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.TreeSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LifeManagerApplication extends Application {
    private DataManager dataManager;
    private NotificationChannel channel;
    private NotificationManagerCompat notificationManager;
    private NotificationCompat.Builder builder;
    private static final String CHANNEL_ID = "Life Manager";
    private Bitmap icon;
    public Calendar autoSyncTime;
    public String autoSyncDescription;

    @Override
    public void onCreate() {
        super.onCreate();
        dataManager = DataManager.getInstance();
        readDate();
        dataManager.renewWork();

        autoSyncTime =Calendar.getInstance();
        autoSyncDescription=getString(R.string.network_description_sync_error);

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
        MessageReceiveThread getMessageThread = new MessageReceiveThread(this);
        getMessageThread.start();
    }




    public String workSend(String uuid) {
        Gson gson = new Gson();
        Work work = dataManager.getWork(uuid);
        String data = gson.toJson(work);

        Log.e("Send", data);
        return data;
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
        }
        try {
            in = new ObjectInputStream(openFileInput("workList.data"));
            dataManager.setWorkList((TreeSet<Work>) in.readObject());
            in.close();
            Log.e("workList.data", dataManager.getWorkList().size() + "");
        } catch (Exception e) {
            dataManager.setWorkList(new TreeSet<>());
        }
        try {

            in = new ObjectInputStream(openFileInput("preference.data"));
            dataManager.setPreference((Preference) in.readObject());
            in.close();
        } catch (Exception e) {
            dataManager.setPreference(new Preference());
        }
        try {
            in = new ObjectInputStream(openFileInput("user.data"));
            dataManager.setUser((User) in.readObject());
            in.close();
        } catch (Exception e) {
            dataManager.setUser(new User());
        }
        try {
            in = new ObjectInputStream(openFileInput("contactList.data"));
            dataManager.setContactList((TreeSet<Contact>) in.readObject());
            in.close();
            Log.e("contactList.data", dataManager.getContactList().size() + "");
        } catch (Exception e) {
            dataManager.setContactList(new TreeSet<>());
        }
        try {
            in = new ObjectInputStream(openFileInput("deletedList.data"));
            dataManager.setDeletedList((TreeSet<String>) in.readObject());
            in.close();
            Log.e("deletedList.data", dataManager.getDeletedList().size() + "");
        } catch (Exception e) {
            dataManager.setDeletedList(new TreeSet<>());
        }
        if (exception != null) {
            Log.e("Read Error", exception.toString());
        }
    }

    public void saveData() {
        ObjectOutput out;
        Log.e("Date Operation", "Write");
        try {
            out = new ObjectOutputStream(openFileOutput("billList.data", MODE_PRIVATE));
            out.writeObject(dataManager.getBills());
            Log.e("billList.data", dataManager.getBills().size() + "");
            out.close();

            out = new ObjectOutputStream(openFileOutput("workList.data", MODE_PRIVATE));
            out.writeObject(dataManager.getWorks());
            Log.e("workList.data", dataManager.getWorks().size() + "");
            out.close();

            out = new ObjectOutputStream(openFileOutput("preference.data", MODE_PRIVATE));
            out.writeObject(dataManager.getPreference());
            out.close();

            out = new ObjectOutputStream(openFileOutput("user.data", MODE_PRIVATE));
            out.writeObject(dataManager.getUser());
            out.close();

            out = new ObjectOutputStream(openFileOutput("contactList.data", MODE_PRIVATE));
            out.writeObject(dataManager.getContacts());
            Log.e("contactList.data", dataManager.getContacts().size() + "");
            out.close();

            out = new ObjectOutputStream(openFileOutput("deletedList.data", MODE_PRIVATE));
            out.writeObject(dataManager.getDeletedList());
            Log.e("deletedList.data", dataManager.getContacts().size() + "");
            out.close();
        } catch (Exception e) {
            Log.e("Write Error", e.toString());
        }
    }

    public void onAutoSync() {
        User user= dataManager.getUser();
        Preference preference=dataManager.getPreference();
        if (user.isValidation() && preference.isAutoSync()) {


            Call<DownloadResponse> call = RequestService.API.sync(new UploadRequest(user.getUuid(), user.getSession(), dataManager.onUpload()));
            call.enqueue(new Callback<DownloadResponse>() {
                @Override
                public void onResponse(Call<DownloadResponse> call, Response<DownloadResponse> response) {
                    autoSyncTime =Calendar.getInstance();

                    try {
                        DownloadResponse body = response.body();
                        autoSyncDescription=getString(NetworkDescriptionRes.SYNC[body.state]);
                        if (body.state == DownloadResponse.OK) {
                            dataManager.onDownload(body.getData());

                        }
                    } catch (Exception e) {
                        autoSyncDescription = getString(R.string.network_error);
                    }
                }

                @Override
                public void onFailure(Call<DownloadResponse> call, Throwable t) {
                    autoSyncTime = Calendar.getInstance();
                    autoSyncDescription = getString(R.string.network_error);
                }
            });

        }

    }
}
