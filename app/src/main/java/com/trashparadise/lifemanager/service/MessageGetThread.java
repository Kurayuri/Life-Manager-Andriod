package com.trashparadise.lifemanager.service;

import android.app.Application;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trashparadise.lifemanager.DataManager;
import com.trashparadise.lifemanager.LifeManagerApplication;
import com.trashparadise.lifemanager.bean.Work;
import com.trashparadise.lifemanager.bean.PackReceiveBean;
import com.trashparadise.lifemanager.util.RequestUtils;

import java.lang.reflect.Type;
import java.util.Map;

public class MessageGetThread extends Thread {
    private LifeManagerApplication application;
    private DataManager dataManager;
    private Gson gson;

    public MessageGetThread(Application application) {
        super();
        this.application = (LifeManagerApplication) application;
        dataManager=DataManager.getInstance();
    }

    public void run() {
        gson = new Gson();
        while (true) {
            try {
                String a = RequestUtils.recv(dataManager.getUser().getUuid());
                Log.e("Get",a);

                try {
                    Type tom = new TypeToken<Map<String, PackReceiveBean>>() {
                    }.getType();
                    Map<String, PackReceiveBean> nmp = gson.fromJson(a, tom);
                    for (Map.Entry<String, PackReceiveBean> entry : nmp.entrySet()) {
                        String json=entry.getValue().getJson();
                        Work work=gson.fromJson(json,Work.class);
                        application.workReceive(work);
                    }
//                }
                }catch (Exception e){
                    Log.e("Get Error",e.toString());
                }
                Thread.sleep(5000);
            } catch (Exception e) {

            }
        }
    }
}
