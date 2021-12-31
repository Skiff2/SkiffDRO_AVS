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

    //Применение значения линейки по X
    public void setScalesValX(Double ScalesValX) {
        this.ScalesValX = ScalesValX;
        X = Utils.ValToPrint((ScalesValX-ScalesOffsetX));

        if (ScalesDsetX == 0)
            D = "---";
        else
            D = Utils.ValToPrint(ScalesDsetX - (ScasesDfixX - ScalesValX)*-1);
    }

    //Применение значения линейки по Z
    public void setScalesValZ(Double ScalesValY) {
        this.ScalesValY = ScalesValY;
        Z = Utils.ValToPrint((ScalesValY-ScalesOffsetY));

        if (ScalesDsetY == 0)
            L = "---";
        else
            L = Utils.ValToPrint(ScalesDsetY - (ScasesDfixY - ScalesValY)*-1);
    }

    //Установка диаметра от ТЕКУЩЕЙ позиции
    public void setD(double ScalesDsetX)
    {
        this.ScalesDsetX = ScalesDsetX;
        ScasesDfixX = ScalesValX;
    }

    //Установка диаметра с оффсетом позиции
    public void setD(double ScalesDsetX, double ScasesDfixX)
    {
        this.ScalesDsetX = ScalesDsetX;
        this.ScasesDfixX = ScasesDfixX;
    }

    //Устаовка длины от ТЕКУЩЕЙ позиции
    public void setL(double ScalesDsetY)
    {
        this.ScalesDsetY = ScalesDsetY;
        ScasesDfixY = ScalesValY;
    }
    //Установка длины с оффсетом позиции
    public void setL(double ScalesDsetY, double ScasesDfixY)
    {
        this.ScalesDsetY = ScalesDsetY;
        this.ScasesDfixY = ScasesDfixY;
    }

    //Обнуление относительных координат
    public void SetX0(){ScalesOffsetX = ScalesValX;}
    public void SetZ0(){ScalesOffsetY = ScalesValY;}
}
