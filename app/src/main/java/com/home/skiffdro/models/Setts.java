package com.home.skiffdro.models;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

public class Setts {

    private static SharedPreferences prefs;
    public static Setts instance;

    public Setts(){}

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
    public void setIsLandscape(boolean val) { prefs.edit().putBoolean("IsPortret", !val).apply(); }

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

    public int iLatheAxisBigNameSize() {try{ return Math.max(Integer.parseInt(getLatheAxisBigNameSize()),5); } catch (Exception ex){ return 5;} }
    public String getLatheAxisBigNameSize() {return prefs.getString("LatheAxisBigNameSize", "42");  }
    public void setLatheAxisBigNameSize(String val) { prefs.edit().putString("LatheAxisBigNameSize", val).apply(); }

    public int iLatheAxisBigValueSize() {try{ return Math.max(Integer.parseInt(getLatheAxisBigValueSize()),5); } catch (Exception ex){ return 5;} }
    public String getLatheAxisBigValueSize() {return prefs.getString("LatheAxisBigValueSize", "70");  }
    public void setLatheAxisBigValueSize(String val) { prefs.edit().putString("LatheAxisBigValueSize", val).apply(); }

    public int iLatheAxisSmallNameSize() {try{ return Math.max(Integer.parseInt(getLatheAxisSmallNameSize()),5); } catch (Exception ex){ return 5;} }
    public String getLatheAxisSmallNameSize() {return prefs.getString("LatheAxisSmallNameSize", "30");  }
    public void setLatheAxisSmallNameSize(String val) { prefs.edit().putString("LatheAxisSmallNameSize", val).apply(); }

    public int iLatheAxisSmallValueSize() {try{ return Math.max(Integer.parseInt(getLatheAxisSmallValueSize()),5); } catch (Exception ex){ return 5;} }
    public String getLatheAxisSmallValueSize() {return prefs.getString("LatheAxisSmallValueSize", "33");  }
    public void setLatheAxisSmallValueSize(String val) { prefs.edit().putString("LatheAxisSmallValueSize", val).apply(); }

    //Оси фрезерного
    public String getMillingAxisNameX() {return prefs.getString("MillingAxisNameX", "X");  }
    public void setMillingAxisNameX(String val) { prefs.edit().putString("MillingAxisNameX", val).apply(); }

    public String getMillingAxisNameY() {return prefs.getString("MillingAxisNameY", "Y");  }
    public void setMillingAxisNameY(String val) { prefs.edit().putString("MillingAxisNameY", val).apply(); }

    public String getMillingAxisNameZ() {return prefs.getString("MillingAxisNameZ", "Z");  }
    public void setMillingAxisNameZ(String val) { prefs.edit().putString("MillingAxisNameZ", val).apply(); }


    public int iMillingAxisBigNameSize() {try{ return Math.max(Integer.parseInt(getMillingAxisBigNameSize()),5); } catch (Exception ex){ return 5;} }
    public String getMillingAxisBigNameSize() {return prefs.getString("MillingAxisBigNameSize", "42");  }
    public void setMillingAxisBigNameSize(String val) { prefs.edit().putString("MillingAxisBigNameSize", val).apply(); }

    public int iMillingAxisBigValueSize() {try{ return Math.max(Integer.parseInt(getMillingAxisBigValueSize()),5); } catch (Exception ex){ return 5;} }
    public String getMillingAxisBigValueSize() {return prefs.getString("MillingAxisBigValueSize", "70");  }
    public void setMillingAxisBigValueSize(String val) { prefs.edit().putString("MillingAxisBigValueSize", val).apply(); }

    public int iMillingAxisSmallNameSize() {try{ return Math.max(Integer.parseInt(getMillingAxisSmallNameSize()),5); } catch (Exception ex){ return 5;} }
    public String getMillingAxisSmallNameSize() {return prefs.getString("MillingAxisSmallNameSize", "30");  }
    public void setMillingAxisSmallNameSize(String val) { prefs.edit().putString("MillingAxisSmallNameSize", val).apply(); }

    public int iMillingAxisSmallValueSize() {try{ return Math.max(Integer.parseInt(getMillingAxisSmallValueSize()),5); } catch (Exception ex){ return 5;} }
    public String getMillingAxisSmallValueSize() {return prefs.getString("MillingAxisSmallValueSize", "33");  }
    public void setMillingAxisSmallValueSize(String val) { prefs.edit().putString("MillingAxisSmallValueSize", val).apply(); }

    //Элементы расчёта
    public int iItemValueSize() {try{ return Math.max(Integer.parseInt(getItemValueSize()),5); } catch (Exception ex){ return 5;} }
    public String getItemValueSize() {return prefs.getString("ItemValueSize", "26");  }
    public void setItemValueSize(String val) { prefs.edit().putString("ItemValueSize", val).apply(); }
}
