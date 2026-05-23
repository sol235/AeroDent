package com.diploma.aerodent.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "AeroDentSession";
    private static final String KEY_LOGGED_IN_USER_ID = "loggedInUserId";
    private static final String KEY_LOGGED_IN_USER_ROLE = "loggedInUserRole";
    private static final String KEY_RZI_CODE = "rziCode";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void loginUser(String userId, String roleName) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_LOGGED_IN_USER_ID, userId);
        editor.putString(KEY_LOGGED_IN_USER_ROLE, roleName);
        editor.apply();
    }

    public void logoutUser() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_LOGGED_IN_USER_ID);
        editor.remove(KEY_LOGGED_IN_USER_ROLE);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return prefs.getString(KEY_LOGGED_IN_USER_ID, null) != null;
    }

    public String getLoggedInUserId() {
        return prefs.getString(KEY_LOGGED_IN_USER_ID, null);
    }

    public String getLoggedInUserRole() {
        return prefs.getString(KEY_LOGGED_IN_USER_ROLE, null);
    }



    // Global Clinic Settings
    public void saveRziCode(String rziCode) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_RZI_CODE, rziCode);
        editor.apply();
    }

    public String getRziCode() {
        return prefs.getString(KEY_RZI_CODE, "");
    }
}
