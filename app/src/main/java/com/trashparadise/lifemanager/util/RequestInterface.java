package com.trashparadise.lifemanager.util;

import com.trashparadise.lifemanager.bean.network.DownloadRequest;
import com.trashparadise.lifemanager.bean.network.DownloadResponse;
import com.trashparadise.lifemanager.bean.network.LoginRequest;
import com.trashparadise.lifemanager.bean.network.LoginResponse;
import com.trashparadise.lifemanager.bean.network.ReceiveRequest;
import com.trashparadise.lifemanager.bean.network.ReceiveResponse;
import com.trashparadise.lifemanager.bean.network.RegisterRequest;
import com.trashparadise.lifemanager.bean.network.RegisterResponse;
import com.trashparadise.lifemanager.bean.network.SendRequest;
import com.trashparadise.lifemanager.bean.network.SendResponse;
import com.trashparadise.lifemanager.bean.network.UploadRequest;
import com.trashparadise.lifemanager.bean.network.UploadResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RequestInterface {
    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest body);

    @POST("register")
    Call<RegisterResponse> register(@Body RegisterRequest body);

    @POST("upload")
    Call<UploadResponse> upload(@Body UploadRequest body);

    @POST("download")
    Call<DownloadResponse> download(@Body DownloadRequest body);

    @POST("send")
    Call<SendResponse> send(@Body SendRequest body);

    @POST("receive")
    Call<ReceiveResponse> receive(@Body ReceiveRequest body);

    @POST("sync")
    Call<DownloadResponse> sync(@Body UploadRequest body);
}
