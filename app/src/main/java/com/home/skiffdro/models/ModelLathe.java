package com.home.skiffdro.models;

import android.widget.TextView;

import com.home.skiffdro.R;
import com.home.skiffdro.common.Utils;

import java.io.Serializable;

public class ModelLathe implements Serializable {
    public String D, L, X, Z;

    double ScalesOffsetX = 0; //Значение локального обнуления
    double ScalesValX = 0; //текущее АБСОЛЮТНОЕ значение линейки
    double ScalesDsetX = 0; //Установленный размер диаметра
    double ScasesDfixX = 0; //АБСОЛЮТНОЕ значение линейки для установленного диаметра

    double ScalesOffsetY = 0; //Значение локального обнуления
    double ScalesValY = 0; //текущее АБСОЛЮТНОЕ значение линейки
    double ScalesDsetY = 0; //Установленный размер диаметра
    double ScasesDfixY = 0; //АБСОЛЮТНОЕ значение линейки для установленного диаметра

    public void setScalesValX(Double ScalesValX) {
        this.ScalesValX = ScalesValX;
        X = Utils.ValToPrint((ScalesValX-ScalesOffsetX));

        if (ScalesDsetX == 0)
            D = "---";
        else
            D = Utils.ValToPrint(ScalesDsetX - (ScasesDfixX - ScalesValX)*-1);
    }
    public void setScalesValZ(Double ScalesValY) {
        this.ScalesValY = ScalesValY;
        Z = Utils.ValToPrint((ScalesValY-ScalesOffsetY));

        if (ScalesDsetY == 0)
            L = "---";
        else
            L = Utils.ValToPrint(ScalesDsetY - (ScasesDfixY - ScalesValY)*-1);
    }

    public void setD(double ScalesDsetX)
    {
        this.ScalesDsetX = ScalesDsetX;
        ScasesDfixX = ScalesValX;
    }

    public void setL(double ScalesDsetY)
    {
        this.ScalesDsetY = ScalesDsetY;
        ScasesDfixY = ScalesValY;
    }

    public void SetX0(){ScalesOffsetX = ScalesValX;}
    public void SetZ0(){ScalesOffsetY = ScalesValY;}
}
