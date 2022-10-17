package com.fusoft.walkboner.security;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.fusoft.walkboner.utils.UidGenerator;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encrypt {
    private static SharedPreferences salt;

    private static Context context;

    public static String EncryptSHAPassword(String password) {
        salt = context.getSharedPreferences("saltPass", Activity.MODE_PRIVATE);

        if (salt.getString("salt", "").toString().isEmpty()) {
            salt.edit().putString("salt", UidGenerator.Generate(8)).apply();
        }

        String generatedPassword = null;
        String saltString = salt.getString("salt", "");
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(saltString.getBytes(StandardCharsets.UTF_8));
            byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    public static void setContext(Context cx) {
        context = cx;
    }
}
