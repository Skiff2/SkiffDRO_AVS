package com.home.skiffdro.common;

public final class Utils {
    public static String ValToPrint(double val) {
        if (val == 0) val = 0; //Ээээ... надо ))) Ну типа, избавление от -0 =)
        return String.format("%.2f", val);
    }
}
