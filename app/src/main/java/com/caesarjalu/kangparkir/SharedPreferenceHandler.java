package com.caesarjalu.kangparkir;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferenceHandler {
    static final String KEY_PARKING_COUNT = "parking_count";
    static final String KEY_PARKING_NAME = "parking_name";
    static final String KEY_PARKING_PRICE = "parking_price";

    private static SharedPreferences getSharedPreference(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setParkingCount(Context context, int count) {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putInt(KEY_PARKING_COUNT, count);
        editor.apply();
    }

    public static int getParkingCount(Context context) {
        return getSharedPreference(context).getInt(KEY_PARKING_COUNT, 0);
    }

    public static String getParkingName(Context context) {
        return getSharedPreference(context).getString(KEY_PARKING_NAME, "Parkir Lorem Ipsum");
    }

    public static String getParkingPrice(Context context) {
        return getSharedPreference(context).getString(KEY_PARKING_PRICE, "2000");
    }

    public static void resetParkingCount(Context context) {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putInt(KEY_PARKING_COUNT, 0);
        editor.apply();
    }
}
