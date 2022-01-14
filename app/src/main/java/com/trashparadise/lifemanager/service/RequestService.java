package com.trashparadise.lifemanager.service;

import com.trashparadise.lifemanager.util.RequestInterface;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RequestService {
    public static String address ="http://10.0.2.2:8080/";
    public static RequestInterface API;
    static {
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(address)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        API =retrofit.create(RequestInterface.class);
    }
}


