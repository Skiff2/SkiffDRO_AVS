package com.home.skiffdro.models;

import android.util.TypedValue;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.home.skiffdro.common.Utils;

public class ModelLathe {

    public LatheTool Tools[] = {new LatheTool(), new LatheTool(), new LatheTool(), new LatheTool()};
    public LatheTool SelTool = Tools[0];
    public int ToolNum = 1;

    public boolean ZasABS = false;

    public String D, L, X, Z; //Экранные оторбажаемые значения

    //double ScalesOffsetX = 0; //Значение локального обнуления
    double ScalesValX = 0; //текущее АБСОЛЮТНОЕ значение линейки
    //public double ScalesDsetX = 0; //Установленный размер диаметра
    //public double ScalesDfixX = 0; //АБСОЛЮТНОЕ значение линейки для установленного диаметра

    double ScalesOffsetZ = 0; //Значение локального обнуления
    double ScalesValZ = 0; //текущее АБСОЛЮТНОЕ значение линейки
    public double ScalesLsetZ = 0; //Установленный размер диаметра
    public double ScalesLfixZ = 0; //АБСОЛЮТНОЕ значение линейки для установленного диаметра

    //Применение значения линейки по X
    public void setScalesValX(Double ScalesValX) {
        this.ScalesValX = ScalesValX;
        X = Utils.ValToPrint((ScalesValX-SelTool.ScalesOffsetX));

        if (SelTool.ScalesDsetX == 0)
            D = "---";
        else
            D = Utils.ValToPrint(SelTool.ScalesDsetX - (SelTool.ScalesDfixX - ScalesValX)*-1);
    }

    //Применение значения линейки по Z
    public void setScalesValZ(Double ScalesValZ) {
        this.ScalesValZ = ScalesValZ;
        Z = Utils.ValToPrint(ZasABS? Math.abs(ScalesValZ-ScalesOffsetZ):(ScalesValZ-ScalesOffsetZ));

        if (ScalesLsetZ == 0)
            L = "---";
        else
            L = Utils.ValToPrint(ScalesLsetZ - (ScalesLfixZ - ScalesValZ)*-1);
    }

    //Установка диаметра от ТЕКУЩЕЙ позиции
    public void setD(double ScalesDsetX)
    {
        SelTool.ScalesDsetX = ScalesDsetX;
        SelTool.ScalesDfixX = ScalesValX;
    }

    //Установка диаметра с оффсетом позиции
    public void setD(double ScalesDsetX, double ScasesDfixX)
    {
        SelTool.ScalesDsetX = ScalesDsetX;
        SelTool.ScalesDfixX = ScasesDfixX;
    }

    //Установка длины от ТЕКУЩЕЙ позиции
    public void setL(double ScalesDsetZ)
    {
        this.ScalesLsetZ = ScalesDsetZ;
        ScalesLfixZ = ScalesValZ;
    }
    //Установка длины с оффсетом позиции
    public void setL(double ScalesLsetZ, double ScasesLfixZ)
    {
        this.ScalesLsetZ = ScalesLsetZ;
        this.ScalesLfixZ = ScasesLfixZ;
    }

    @BindingAdapter("android:textSize")
    public static void bindTextSize(TextView textView, int size) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
    }

    // Инструменты
    public void selToolNum(int ToolNum){
        this.ToolNum = ToolNum;
        SelTool = Tools[ToolNum-1];
    }

    //Обнуление относительных координат
    public void SetX0(){SelTool.ScalesOffsetX = ScalesValX;}
    public void SetZ0(){ScalesOffsetZ = ScalesValZ;}

    //Установлены ли абсолютные значения диаметра и длины
    public boolean DSetted() { return (SelTool.ScalesDsetX != 0);}
    public boolean LSetted() { return (ScalesLsetZ != 0);}

    //Получение привязанных координат
    public double getXval(){ return ScalesValX-SelTool.ScalesOffsetX; }
    public double getDval(){ return SelTool.ScalesDsetX - (SelTool.ScalesDfixX - ScalesValX)*-1; }
    public double getZval(){ return ScalesValZ-ScalesOffsetZ; }
    public double getLval(){ return ScalesLsetZ - (ScalesLfixZ - ScalesValZ)*-1; }
}
