package com.trashparadise.lifemanager.util;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidUtils {
    // Password not empty
    public static boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 0;
    }

}
