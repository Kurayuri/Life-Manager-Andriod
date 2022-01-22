package com.trashparadise.lifemanager.service;

import android.app.Application;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trashparadise.lifemanager.DataManager;
import com.trashparadise.lifemanager.LifeManagerApplication;
import com.trashparadise.lifemanager.bean.Work;
import com.trashparadise.lifemanager.bean.network.ReceiveRequest;
import com.trashparadise.lifemanager.bean.network.ReceiveResponse;

import java.lang.reflect.Type;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageReceiveThread extends Thread {
    private LifeManagerApplication application;
    private DataManager dataManager;
    private Gson gson;

    public MessageReceiveThread(Application application) {
        super();
        this.application = (LifeManagerApplication) application;
        dataManager = DataManager.getInstance();
    }

    public void run() {
        gson = new Gson();
        while (true) {
            try {
                Call<ReceiveResponse> call = RequestService.API.receive(new ReceiveRequest(dataManager.getUser().getUuid(), dataManager.getUser().getSession()));
                call.enqueue(new Callback<ReceiveResponse>() {
                    @Override
                    public void onResponse(Call<ReceiveResponse> call, Response<ReceiveResponse> response) {
                        try {
                            ReceiveResponse body = response.body();
                            Log.e("Receive","-"+body.description);
                            if (body.state == ReceiveResponse.OK) {
                                String data = body.getData();
                                ArrayList<String> strs = gson.fromJson(data, new TypeToken<ArrayList<String>>() {
                                }.getType());
                                for (String str : strs) {
                                    Work work = gson.fromJson(str, Work.class);
                                    application.workReceive(work);
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Receive Error", e.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<ReceiveResponse> call, Throwable t) {

                    }
                });
                Thread.sleep(5000);
            } catch (Exception e) {

            }
        }
    }
}
