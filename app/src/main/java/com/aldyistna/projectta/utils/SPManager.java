package com.aldyistna.projectta.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class SPManager {

    private static final String SP_APP = "laporan";

    public static final String SP_USER = "userObject";
    public static final String SP_USER_ID = "userId";
    public static final String SP_USER_NAME = "userName";
    public static final String SP_USER_ROLE = "userRole";
    public static final String SP_LOGIN = "login";

    private final SharedPreferences sp;
    private final SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    public SPManager(Context context) {
        sp = context.getSharedPreferences(SP_APP, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public void saveString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public void saveInt(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public void saveBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public String getSpUser() {
        return sp.getString(SP_USER, "");
    }

    public String getSpUserName() {
        return sp.getString(SP_USER_NAME, "");
    }

    public String getSpUserRole() {
        return sp.getString(SP_USER_ROLE, "");
    }

    public int getSpUserId() {
        return sp.getInt(SP_USER_ID, 0);
    }

    public boolean getSPLogin() {
        return sp.getBoolean(SP_LOGIN, false);
    }
}
