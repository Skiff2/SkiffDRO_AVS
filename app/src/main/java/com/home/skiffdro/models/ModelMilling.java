package com.home.skiffdro.models;

import com.home.skiffdro.common.Utils;

import java.io.Serializable;

public class ModelMilling implements Serializable {
    public Setts Setts;

    public String strCX, strCY, strX, strY, strZ; //Отображаемое
    public boolean ShowResetX = true, ShowResetY = true;

    public double ScalesOffsetX = 0; //Значение локального обнуления
    double ScalesValX = 0; //текущее АБСОЛЮТНОЕ значение линейки

    public double ScalesOffsetY = 0; //Значение локального обнуления
    double ScalesValY = 0; //текущее АБСОЛЮТНОЕ значение линейки

    public double ScalesOffsetZ = 0; //Значение локального обнуления
    double ScalesValZ = 0; //текущее АБСОЛЮТНОЕ значение линейки

    public double CenterX; //Координатата центра по Х
    public double CenterY; //Коорината центра по Y

    public double X1 = -9000, X2 = -9000, Y1 = -9000, Y2 = -9000; //Коррдинаты центрирования

////////////////////////////////////////////////Внешние входные данные
    //Установка значения линейки по X
    public void setScalesValX(double val){
        ScalesValX = val;
        strX = Utils.ValToPrint(ScalesValX-ScalesOffsetX);

        if (X1Setted() && X2Setted()) {
            double x1 = Math.min(X1, X2), x2 = Math.max(X1, X2);
            CenterX = x1 + ((x2-x1)/2);
            strCX = Utils.ValToPrint((ScalesValX - CenterX));
        }
        else
            strCX = "---";
    }
    //Установка значения линейки по Y
    public void setScalesValY(double val){
        ScalesValY = val;
        strY = Utils.ValToPrint(ScalesValY-ScalesOffsetY);

        if (Y1Setted() && Y2Setted()) {
            double y1 = Math.min(Y1, Y2), y2 = Math.max(Y1, Y2);
            CenterY = y1 + ((y2-y1)/2);
            strCY = Utils.ValToPrint((ScalesValY - CenterY));
        }
        else
            strCY = "---";

    }

    //Установка значения линейки по Z
    public void setScalesValZ(double val){
        ScalesValZ = val;
        strZ = Utils.ValToPrint(ScalesValZ-ScalesOffsetZ);
    }
///////////////////////////////////////////////////

    //Обнуление относительных координат
    public void SetX0(){ScalesOffsetX = ScalesValX;}
    public void SetY0(){ScalesOffsetY = ScalesValY;}
    public void SetZ0(){ScalesOffsetZ = ScalesValZ;}

    //Установка точек координат для вычисления центра
    public void SetX1(){X1 = ScalesValX;}
    public void SetX2(){X2 = ScalesValX;}
    public void SetY1(){Y1 = ScalesValY;}
    public void SetY2(){Y2 = ScalesValY;}

    //Установены ли точки координат для вычисления центра
    public boolean X1Setted() { return X1 != -9000; }
    public boolean X2Setted() { return X2 != -9000; }
    public boolean Y1Setted() { return Y1 != -9000; }
    public boolean Y2Setted() { return Y2 != -9000; }

    //Отображение кнопок сброса
    public void setShowResetX(boolean showResetX) {
        ShowResetX = showResetX;
    }
    public void setShowResetY(boolean showResetY) {
        ShowResetY = showResetY;
    }

    //Получение привязанных координат
    public double getXval(){ return ScalesValX-ScalesOffsetX; }
    public double getYval(){ return ScalesValY-ScalesOffsetY; }
    public double getZval(){ return ScalesValZ-ScalesOffsetZ; }
}
