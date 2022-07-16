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

    //Показывать 4 инстурмента в токарном режиме
    public boolean getIsShow4LatheTool() {return prefs.getBoolean("IsShow4LatheTool", true);  }
    public void setIsShow4LatheTool(boolean val) { prefs.edit().putBoolean("IsShow4LatheTool", val).apply(); }

    //Использовать USB подключение
    public boolean getIsUseUSB() {return prefs.getBoolean("IsUseUSB", true);  }
    public void setIsUseUSB(boolean val) { prefs.edit().putBoolean("IsUseUSB", val).apply(); }

    //Список БТ устройств для выбора
    public void setBTDevicesList (Set<String> BTList) {prefs.edit().putStringSet("SelectedBTDevices", BTList).apply(); }
    public Set<String> getBTDevicesList() { return prefs.getStringSet("SelectedBTDevices",null); }
}
