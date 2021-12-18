package com.trashparadise.lifemanager.service;

import android.app.Application;
import android.util.Log;

import com.trashparadise.lifemanager.LifeManagerApplication;

public class MessageGETThread extends Thread{
    private LifeManagerApplication application;
    public MessageGETThread(Application app){
        super();
        application=(LifeManagerApplication)app;
    }
    public void run(){
        while (true){
//            application.Msg("6dec6a992e8649f080a894b743fb8a86");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
