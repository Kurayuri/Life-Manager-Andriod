package com.trashparadise.lifemanager.util;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidUtils {


    // 校验密码不少于6位
    public static boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    // 性别只能填1或2
    public static boolean isGenderValid(String gender) {
        return gender.equals("1") || gender.equals("2");
    }

    /**
     * MD5加密+BASE64编码
     *
     * @return 加密后字符串
     */
    public static String encodeByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        // 注意这里是 Base64.NO_WRAP，不能用 Base64.DEFAULT，否则结尾会带一个 \n
        String newstr = new String(Base64.encode(md5.digest(str.getBytes("utf-8")), Base64.NO_WRAP));
        return newstr;
    }
}
