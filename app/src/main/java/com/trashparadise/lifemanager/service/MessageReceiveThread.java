package com.trashparadise.lifemanager.service;

import android.app.Application;
import android.util.Log;

import com.google.gson.Gson;
import com.trashparadise.lifemanager.DataManager;
import com.trashparadise.lifemanager.LifeManagerApplication;
import com.trashparadise.lifemanager.bean.Work;
import com.trashparadise.lifemanager.bean.network.ReceiveRequest;
import com.trashparadise.lifemanager.bean.network.ReceiveResponse;

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
                            ArrayList<String> data = body.getData();
                            Log.e("Receive", data.toString());
                            for (String str : data) {
                                Work work = gson.fromJson(str, Work.class);
                                application.workReceive(work);
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