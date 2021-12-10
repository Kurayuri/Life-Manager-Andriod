package com.trashparadise.lifemanager.service;


import android.app.AlarmManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.ArrayMap;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.trashparadise.lifemanager.LifeManagerApplication;
import com.trashparadise.lifemanager.MainActivity;
import com.trashparadise.lifemanager.R;
import com.trashparadise.lifemanager.Work;

import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

public class NotificationThread extends Thread {
    private int id;
    private ArrayList<Work> workList;
    private ArrayList<Work> workListNew;
    private ArrayMap<String,Work> workNotified;
    private LifeManagerApplication application;


    public NotificationThread(Application app){
        application=(LifeManagerApplication)app;
        workNotified=new ArrayMap<>();
        id=0;
    }

    @Override
    public void run() {
        super.run();
        while (true) {

            try {
                workListNew = application.getWorkList();
            } catch (Exception e) {
                workListNew = null;
            }
            if (workListNew != null) {
                workList = workListNew;
            }

            Date date = new Date();
            for (Work work : workList) {
                if (work.getDate().getTime().getTime() - date.getTime() < -10 && work.getForm().equals(Work.TODO) && workNotified.get(work.getUuid()) == null) {
                    workNotified.put(work.getUuid(), work);
                    application.Notify(id,work.getUuid());
                    id++;
                }
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}