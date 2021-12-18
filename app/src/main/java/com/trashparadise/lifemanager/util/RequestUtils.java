package com.trashparadise.lifemanager.util;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class RequestUtils {
    public static String address ="http://121.43.138.1:5000/";

    
    private static String post(String s, String t) {
        try {
            URL url = new URL(s);
            URLConnection connection = url.openConnection();
            connection.setDoOutput(true);
            connection.connect();
            OutputStream out = connection.getOutputStream();
            out.write(t.getBytes(StandardCharsets.UTF_8));
            out.flush();
            out.close();
            InputStream in = connection.getInputStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] buf = new byte[128];
            int len;
            while ((len = in.read(buf, 0, 128)) >= 0) {
                buffer.write(buf,0,len);
            }
            in.close();
            return buffer.toString();
//            return ss.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "0000000000000000000000000000000000000000";
        }
    }
    public static String download(String src) {
        String s = "src=" + src;
        return post(address +"download", s);
    }
    public static String login(String account, String password) {
        String s = "account=" + account + "&password=" + password;
        return post(address +"login", s);
    }
    public static String recv(String dst) {
        String s = "dst=" + dst;
        return post(address +"recv", s);
    }
    public static String register(String account, String password) {
        String s = "account=" + account + "&password=" + password;
        return post(address +"register", s);
    }
    public static String send(String src, String dst, String json) {
        String s = "src=" + src + "&dst=" + dst + "&json=" + json;
        return post(address +"send", s);
    }
    public static String upload(String src, String data) {
        String s = "src=" + src + "&data=" + data;
        return post(address +"upload", s);
    }
}