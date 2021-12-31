package com.home.skiffdro.models;

import android.widget.TextView;

import com.home.skiffdro.R;
import com.home.skiffdro.common.Utils;

import java.io.Serializable;

public class ModelLathe implements Serializable {
    public String D, L, X, Z;

    double ScalesOffsetX = 0; //Значение локального обнуления
    double ScalesValX = 0; //текущее АБСОЛЮТНОЕ значение линейки
    public double ScalesDsetX = 0; //Установленный размер диаметра
    public double ScalesDfixX = 0; //АБСОЛЮТНОЕ значение линейки для установленного диаметра

    double ScalesOffsetZ = 0; //Значение локального обнуления
    double ScalesValZ = 0; //текущее АБСОЛЮТНОЕ значение линейки
    public double ScalesLsetZ = 0; //Установленный размер диаметра
    public double ScasesLfixZ = 0; //АБСОЛЮТНОЕ значение линейки для установленного диаметра

    //Применение значения линейки по X
    public void setScalesValX(Double ScalesValX) {
        this.ScalesValX = ScalesValX;
        X = Utils.ValToPrint((ScalesValX-ScalesOffsetX));

        if (ScalesDsetX == 0)
            D = "---";
        else
            D = Utils.ValToPrint(ScalesDsetX - (ScalesDfixX - ScalesValX)*-1);
    }

    //Применение значения линейки по Z
    public void setScalesValZ(Double ScalesValY) {
        this.ScalesValZ = ScalesValY;
        Z = Utils.ValToPrint((ScalesValY-ScalesOffsetZ));

        if (ScalesLsetZ == 0)
            L = "---";
        else
            L = Utils.ValToPrint(ScalesLsetZ - (ScasesLfixZ - ScalesValY)*-1);
    }

    //Установка диаметра от ТЕКУЩЕЙ позиции
    public void setD(double ScalesDsetX)
    {
        this.ScalesDsetX = ScalesDsetX;
        ScalesDfixX = ScalesValX;
    }

    //Установка диаметра с оффсетом позиции
    public void setD(double ScalesDsetX, double ScasesDfixX)
    {
        this.ScalesDsetX = ScalesDsetX;
        this.ScalesDfixX = ScasesDfixX;
    }

    //Устаовка длины от ТЕКУЩЕЙ позиции
    public void setL(double ScalesDsetY)
    {
        this.ScalesLsetZ = ScalesDsetY;
        ScasesLfixZ = ScalesValZ;
    }
    //Установка длины с оффсетом позиции
    public void setL(double ScalesDsetY, double ScasesDfixY)
    {
        this.ScalesLsetZ = ScalesDsetY;
        this.ScasesLfixZ = ScasesDfixY;
    }

    //Обнуление относительных координат
    public void SetX0(){ScalesOffsetX = ScalesValX;}
    public void SetZ0(){ScalesOffsetZ = ScalesValZ;}

    //Установлены ли абсолютные значения диаметра и длины
    public boolean DSetted() { return (ScalesDsetX == 0);}
    public boolean LSetted() { return (ScalesLsetZ == 0);}
}
