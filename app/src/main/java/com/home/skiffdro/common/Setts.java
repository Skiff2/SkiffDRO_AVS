package com.home.skiffdro.common;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

public class Setts {

    private static SharedPreferences prefs;

    private static Setts instance;

    //для первой инициализации
    public static synchronized Setts getInstance(Context context) {
        if (instance == null) {
            instance = new Setts();
            prefs = context.getSharedPreferences("SkiffDRO", Context.MODE_PRIVATE);
        }
        return instance;
    }
    //Для остальной работы
    public static synchronized Setts getInstance() {
        return instance;
    }

    //Портретный режим
    public boolean getIsPortret() {return prefs.getBoolean("IsPortret", true);  }
    public void setIsPortret(boolean val) { prefs.edit().putBoolean("IsPortret", val).apply(); }

    //Список БТ устройств для выбора
    public void setBTDevicesList (Set<String> BTList) {prefs.edit().putStringSet("SelectedBTDevices", BTList).apply(); }
    public Set<String> getBTDevicesList() { return prefs.getStringSet("SelectedBTDevices",null); }
}
