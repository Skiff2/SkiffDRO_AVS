package com.home.skiffdro.models;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

public class Setts {

    private static SharedPreferences prefs;
    public static Setts instance;

    //для первой инициализации
    public Setts(Context context)
    {
        if (instance == null) {
            instance = this;
            prefs = context.getSharedPreferences("SkiffDRO", Context.MODE_PRIVATE);
        }
    }

    //Портретный режим
    public boolean getIsPortret() {return prefs.getBoolean("IsPortret", true);  }
    public void setIsPortret(boolean val) { prefs.edit().putBoolean("IsPortret", val).apply(); }

    //Портретный режим
    public boolean getIsLandscape() {return !prefs.getBoolean("IsPortret", false);  }
    public void setLandscape(boolean val) { prefs.edit().putBoolean("IsPortret", !val).apply(); }

    //Показывать 4 инстурмента в токарном режиме
    public boolean getIsShow4LatheTool() {return prefs.getBoolean("IsShow4LatheTool", true);  }
    public void setIsShow4LatheTool(boolean val) { prefs.edit().putBoolean("IsShow4LatheTool", val).apply(); }

    //Использовать USB подключение
    public boolean getIsUseUSB() {return prefs.getBoolean("IsUseUSB", true);  }
    public void setIsUseUSB(boolean val) { prefs.edit().putBoolean("IsUseUSB", val).apply(); }

    //Разворачиваться на весь экран
    public boolean getIsUseFullScreen() {return prefs.getBoolean("IsUseFullScreen", false);  }
    public void setIsUseFullScreen(boolean val) { prefs.edit().putBoolean("IsUseFullScreen", val).apply(); }

    //Автозапуск
    public boolean getIsAutostart() {return prefs.getBoolean("IsAutostart", false);  }
    public void setIsAutostart(boolean val) { prefs.edit().putBoolean("IsAutostart", val).apply(); }

    //Список БТ устройств для выбора
    public void setBTDevicesList (Set<String> BTList) {prefs.edit().putStringSet("SelectedBTDevices", BTList).apply(); }
    public Set<String> getBTDevicesList() { return prefs.getStringSet("SelectedBTDevices",null); }


    //Оси токарного
    public String getLatheAxisNameX() {return prefs.getString("LatheAxisNameX", "X");  }
    public void setLatheAxisNameX(String val) { prefs.edit().putString("LatheAxisNameX", val).apply(); }

    public String getLatheAxisNameZ() {return prefs.getString("LatheAxisNameZ", "Z");  }
    public void setLatheAxisNameZ(String val) { prefs.edit().putString("LatheAxisNameZ", val).apply(); }

    public String getLatheAxisNameD() {return prefs.getString("LatheAxisNameD", "D");  }
    public void setLatheAxisNameD(String val) { prefs.edit().putString("LatheAxisNameD", val).apply(); }

    public String getLatheAxisNameL() {return prefs.getString("LatheAxisNameL", "L");  }
    public void setLatheAxisNameL(String val) { prefs.edit().putString("LatheAxisNameL", val).apply(); }


    public int getLatheAxisBigNameSize() {return prefs.getInt("LatheAxisBigNameSize", 42);  }
    public void setLatheAxisBigNameSize(int val) { prefs.edit().putInt("LatheAxisBigNameSize", val).apply(); }

    public int getLatheAxisBigValueSize() {return prefs.getInt("LatheAxisBigValueSize", 70);  }
    public void setLatheAxisBigValueSize(int val) { prefs.edit().putInt("LatheAxisBigValueSize", val).apply(); }

    public int getLatheAxisSmallNameSize() {return prefs.getInt("LatheAxisSmallNameSize", 30);  }
    public void setLatheAxisSmallNameSize(int val) { prefs.edit().putInt("LatheAxisSmallNameSize", val).apply(); }

    public int getLatheAxisSmallValueSize() {return prefs.getInt("LatheAxisSmallValueSize", 33);  }
    public void setLatheAxisSmallValueSize(int val) { prefs.edit().putInt("LatheAxisSmallValueSize", val).apply(); }

    //Оси фрезерного
    public String getMillingAxisNameX() {return prefs.getString("MillingAxisNameX", "X");  }
    public void setMillingAxisNameX(String val) { prefs.edit().putString("MillingAxisNameX", val).apply(); }

    public String getMillingAxisNameY() {return prefs.getString("MillingAxisNameY", "Y");  }
    public void setMillingAxisNameY(String val) { prefs.edit().putString("MillingAxisNameY", val).apply(); }

    public String getMillingAxisNameZ() {return prefs.getString("MillingAxisNameZ", "Z");  }
    public void setMillingAxisNameZ(String val) { prefs.edit().putString("MillingAxisNameZ", val).apply(); }


    public int getMillingAxisBigNameSize() {return prefs.getInt("MillingAxisBigNameSize", 42);  }
    public void setMillingAxisBigNameSize(int val) { prefs.edit().putInt("MillingAxisBigNameSize", val).apply(); }

    public int getMillingAxisBigValueSize() {return prefs.getInt("MillingAxisBigValueSize", 70);  }
    public void setMillingAxisBigValueSize(int val) { prefs.edit().putInt("MillingAxisBigValueSize", val).apply(); }

    public int getMillingAxisSmallNameSize() {return prefs.getInt("MillingAxisSmallNameSize", 30);  }
    public void setMillingAxisSmallNameSize(int val) { prefs.edit().putInt("MillingAxisSmallNameSize", val).apply(); }

    public int getMillingAxisSmallValueSize() {return prefs.getInt("MillingAxisSmallValueSize", 33);  }
    public void setMillingAxisSmallValueSize(int val) { prefs.edit().putInt("MillingAxisSmallValueSize", val).apply(); }





}
