package com.home.skiffdro.models;

import java.text.DecimalFormat;

public class ItemModel {
    private int NN;
    private String NameA;
    private String NameB;
    private double A;
    private double B;
    private boolean Check;
    private boolean Foud;

    public ItemModel(int NN, String NameA, String NameB, double A, double B)
    {
        this.NN = NN;
        this.NameA = NameA;
        this.NameB = NameB;
        this.A = A;
        this.B = B;
        Check = false;
    }

    public int getNN() { return NN; }
    public double getA() { return A; }
    public double getB() { return B; }
    public boolean getCheck() { return Check; }
    public String getNameA() { return NameA;  }
    public String getNameB() { return NameB;  }
    public boolean getFoud() { return Foud; }

    public void setCheck(boolean check) { Check = check; }
    public void setNN(int nn) { NN = nn;}
    public void setA(double A) { this.A = A; }
    public void setB(double B) { this.B = B; }
    public void setFoud(boolean foud) { Foud = foud; }
}
