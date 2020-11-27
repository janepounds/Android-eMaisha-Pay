package com.cabral.emaishapay.app;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * MyAppPrefsManager handles some Prefs of AndroidShopApp Application
 **/

public class MyAppPrefsManager {
    private SharedPreferences sharedPreferences;

    private int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "AndroidShopApp_Prefs";

    private static final String USER_LANGUAGE_ID = "language_ID";
    private static final String USER_LANGUAGE_CODE = "language_Code";
    private static final String CURRENCY_CODE = "currency_code";
    private static final String APPLICATION_VERSION = "application_version";
    private static final String IS_USER_LOGGED_IN = "isLogged_in";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String IS_PUSH_NOTIFICATIONS_ENABLED = "isPushNotificationsEnabled";
    private static final String IS_LOCAL_NOTIFICATIONS_ENABLED = "isLocalNotificationsEnabled";

    private static final String LOCAL_NOTIFICATIONS_TITLE = "localNotificationsTitle";
    private static final String LOCAL_NOTIFICATIONS_DURATION = "localNotificationsDuration";
    private static final String LOCAL_NOTIFICATIONS_DESCRIPTION = "localNotificationsDescription";

    private static final String Skip_For_Again = "skipMessage";

    public MyAppPrefsManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
    }

    public void setUserLanguageId(int langID) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(USER_LANGUAGE_ID, langID);
        editor.apply();
    }

    public Integer getUserLanguageId() {
        return sharedPreferences.getInt(USER_LANGUAGE_ID, 1);
    }

    public void setUserLanguageCode(String langCode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_LANGUAGE_CODE, langCode);
        editor.apply();
    }

    public String getUserLanguageCode() {
        return sharedPreferences.getString(USER_LANGUAGE_CODE, "en");
    }

    public void setCurrencyCode(String currencyCode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CURRENCY_CODE, currencyCode);
        editor.apply();
    }

    public String getCurrencyCode() {
        return sharedPreferences.getString(CURRENCY_CODE, "UGX");
    }

    public String getApplicationVersion() {
        return sharedPreferences.getString(APPLICATION_VERSION, "");
    }

    public void setApplicationVersion(String applicationVersion) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(APPLICATION_VERSION, applicationVersion);
        editor.apply();
    }

    public void setUserLoggedIn(boolean isUserLoggedIn) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_USER_LOGGED_IN, isUserLoggedIn);
        editor.apply();
    }

    public void logInUser() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_USER_LOGGED_IN, true);
        editor.apply();
    }

    public void logOutUser() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_USER_LOGGED_IN, false);
        editor.apply();
    }

    public boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean(IS_USER_LOGGED_IN, false);
    }

    public void setFirstTimeLaunch(boolean isFirstTimeLaunch) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTimeLaunch);
        editor.apply();
    }

    public boolean isFirstTimeLaunch() {
        return sharedPreferences.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setPushNotificationsEnabled(boolean isPushNotificationsEnabled) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_PUSH_NOTIFICATIONS_ENABLED, isPushNotificationsEnabled);
        editor.apply();
    }

    public boolean isPushNotificationsEnabled() {
        return sharedPreferences.getBoolean(IS_PUSH_NOTIFICATIONS_ENABLED, true);
    }

    public void setLocalNotificationsEnabled(boolean isLocalNotificationsEnabled) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_LOCAL_NOTIFICATIONS_ENABLED, isLocalNotificationsEnabled);
        editor.apply();
    }

    public boolean isLocalNotificationsEnabled() {
        return sharedPreferences.getBoolean(IS_LOCAL_NOTIFICATIONS_ENABLED, true);
    }

    public void setLocalNotificationsTitle(String localNotificationsTitle) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LOCAL_NOTIFICATIONS_TITLE, localNotificationsTitle);
        editor.apply();
    }

    public String getLocalNotificationsTitle() {
        return sharedPreferences.getString(LOCAL_NOTIFICATIONS_TITLE, "CdmKart");
    }

    public void setLocalNotificationsDuration(String localNotificationsDuration) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LOCAL_NOTIFICATIONS_DURATION, localNotificationsDuration);
        editor.apply();
    }

    public String getLocalNotificationsDuration() {
        return sharedPreferences.getString(LOCAL_NOTIFICATIONS_DURATION, "day");
    }

    public void setLocalNotificationsDescription(String localNotificationsDescription) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LOCAL_NOTIFICATIONS_DESCRIPTION, localNotificationsDescription);
        editor.apply();
    }

    public String getLocalNotificationsDescription() {
        return sharedPreferences.getString(LOCAL_NOTIFICATIONS_DESCRIPTION, "Check bundle of New Stores");
    }

    public void setSkip_For_Again(boolean isChecked) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Skip_For_Again, isChecked);
        editor.apply();
    }

    public boolean getSkip_For_Again() {
        return sharedPreferences.getBoolean(Skip_For_Again, false);
    }
}
