package com.trashparadise.lifemanager.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.trashparadise.lifemanager.Bill;
import com.trashparadise.lifemanager.LifeManagerApplication;
import com.trashparadise.lifemanager.MainActivity;
import com.trashparadise.lifemanager.R;
import com.trashparadise.lifemanager.Work;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;
import java.util.TreeSet;

public class NotificationService extends Service {
    private int id;
    private ArrayList<Work> workList;
    private ArrayList<Work> workListNew;
    private NotificationChannel channel;
    private NotificationManagerCompat notificationManager;
    private TreeMap<String,Work> workNotified;
    Bitmap icon;
    private static final String CHANNEL_ID = "Life Manager";

    public NotificationService() {
    }

    @Override
    public void onCreate() {
        workList = ((LifeManagerApplication) getApplication()).getWorkList();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
        }
        workNotified=new TreeMap<>();
        super.onCreate();
        id=0;
        icon= BitmapFactory.decodeResource(getResources(),
                R.drawable.icon_notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent i = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);
        notificationManager = NotificationManagerCompat.from(this);
        notificationManager.createNotificationChannel(channel);

        try {
            workListNew = ((LifeManagerApplication) getApplication()).getWorkList();
        } catch (Exception e) {
            workListNew = null;
        }
        if (workListNew != null) {
            workList = workListNew;
        }


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_notification)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setLargeIcon(icon)
                .setPriority(NotificationCompat.PRIORITY_HIGH);


        Date date=new Date();
        for (Work work : workList) {
            if (work.getDate().getTime().getTime()-date.getTime() < -10 && work.getForm().equals(Work.TODO) && workNotified.get(work.getUuid())==null) {
                workNotified.put(work.getUuid(),work);
                builder.setContentTitle(work.getTitle())
                        .setContentText(getString(R.string.need_todo));
                Notification notification = builder.build();
                notificationManager.notify(id,notification);
                id++;
            }
        }

//        Log.e("Notification Service", "Execute");


        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long th = SystemClock.elapsedRealtime() + 5000; // 5 s
        Intent intent1 = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(this, 0, intent1, 0);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, th, pendingIntent1);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return  null;
    }
}