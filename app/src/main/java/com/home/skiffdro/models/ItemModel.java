package com.home.skiffdro.models;

import java.text.DecimalFormat;

public class ItemModel {
    private int NN;
    private String NameS;
    private double X;
    private double S;
    private boolean Check;
    private boolean Foud;

    public ItemModel(int NN, String NameS, double X, double S)
    {
        this.NN = NN;
        this.NameS = NameS;
        this.X = X;
        this.S = S;
        Check = false;
    }

    public int getNN() { return NN; }
    public double getX() { return X; }
    public double getS() { return S; }
    public boolean getCheck() { return Check; }
    public String getNameS() { return NameS;  }
    public boolean getFoud() { return Foud; }

    public void setCheck(boolean check) { Check = check; }
    public void setNN(int nn) {NN = nn;}
    public void setS(double s) {S = s; }
    public void setX(double x) { X = x; }
    public void setFoud(boolean foud) { Foud = foud; }
}
