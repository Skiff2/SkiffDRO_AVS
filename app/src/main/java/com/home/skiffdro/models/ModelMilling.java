package com.home.skiffdro.models;

import com.home.skiffdro.common.Utils;

import java.io.Serializable;

public class ModelMilling implements Serializable {
    public String CX, CY, X, Y, Z; //Отображаемое
    public boolean ShowResetX = true, ShowResetY = true;

    double ScalesOffsetX = 0; //Значение локального обнуления
    double ScalesValX = 0; //текущее АБСОЛЮТНОЕ значение линейки

    double ScalesOffsetY = 0; //Значение локального обнуления
    double ScalesValY = 0; //текущее АБСОЛЮТНОЕ значение линейки

    double ScalesOffsetZ = 0; //Значение локального обнуления
    double ScalesValZ = 0; //текущее АБСОЛЮТНОЕ значение линейки

    public double CenterX;
    public double CenterY;

    public double X1 = -9000, X2 = -9000, Y1 = -9000, Y2 = -9000; //Коррдинаты центрирования

////////////////////////////////////////////////Внешние входные данные
    public void setScalesValX(double val){
        ScalesValX = val;
        X = Utils.ValToPrint(ScalesValX-ScalesOffsetX);

        if (X1Setted() && X2Setted()) {
            double x1 = Math.min(X1, X2), x2 = Math.max(X1, X2);
            CenterX = (x2-x1)/2;
            CX = Utils.ValToPrint((ScalesValX - (x1 + CenterX)));
        }
        else
            CX = "---";
    }
    public void setScalesValY(double val){
        ScalesValY = val;
        Y = Utils.ValToPrint(ScalesValY-ScalesOffsetY);

        if (Y1Setted() && Y2Setted()) {
            double y1 = Math.min(Y1, Y2), y2 = Math.max(Y1, Y2);
            CenterY = (y2-y1)/2;
            CY = Utils.ValToPrint((ScalesValY - (y1 + CenterY)));
        }
        else
            CY = "---";

    }

    public void setScalesValZ(double val){
        ScalesValZ = val;
        Z = Utils.ValToPrint(ScalesValZ-ScalesOffsetZ);
    }
///////////////////////////////////////////////////

    public void SetX0(){ScalesOffsetX = ScalesValX;}
    public void SetY0(){ScalesOffsetY = ScalesValY;}
    public void SetZ0(){ScalesOffsetZ = ScalesValZ;}

    public void SetX1(){X1 = ScalesValX;}
    public void SetX2(){X2 = ScalesValX;}
    public void SetY1(){Y1 = ScalesValY;}
    public void SetY2(){Y2 = ScalesValY;}

    public boolean X1Setted() { return X1 != -9000; }
    public boolean X2Setted() { return X2 != -9000; }
    public boolean Y1Setted() { return Y1 != -9000; }
    public boolean Y2Setted() { return Y2 != -9000; }

    public void setShowResetX(boolean showResetX) {
        ShowResetX = showResetX;
    }

    public void setShowResetY(boolean showResetY) {
        ShowResetY = showResetY;
    }
}
