package com.cabral.emaishapay.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceHelper {
    private final SharedPreferences sharedPreferences;
    private final String animal = "selectedAnimal";
    private final String latitude = "defaultLatitude";
    private final String longitude = "defaultLongitude";

    public SharedPreferenceHelper(Context context) {
        String preferenceName = "SharedPreference";
        sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
    }

    public void insertSelectedAnimal(String selectedAnimal) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(animal, selectedAnimal);
        editor.apply();
    }

    public String getSelectedAnimal() {
        return sharedPreferences.getString(animal, "");
    }

    public void setDefaultAddress(String longitude, String latitude) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(this.latitude, latitude);
        editor.putString(this.longitude, longitude);
        editor.apply();
    }

    public String getDefaultLatitude() {
        return sharedPreferences.getString(latitude, "");
    }

    public String getDefaultLongitude() {
        return sharedPreferences.getString(longitude, "");
    }
}
