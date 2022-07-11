package com.home.skiffdro.models;

public class MarkModel {

    private String Name;
    private String NameA;
    private String NameB;
    private String NameC;
    private double A;
    private double B;
    private double C;
    private boolean Notify;
    private boolean Found;

    public MarkModel(String Name, String NameA, String NameB, String NameC, double A, double B, double C)
    {
        this.Name = Name;
        this.NameA = NameA;
        this.NameB = NameB;
        this.NameC = NameC;
        this.A = A;
        this.B = B;
        this.C = C;
    }

    public double getA() { return A; }
    public double getB() { return B; }
    public double getC() { return C; }
    public boolean getNotify() { return Notify; }
    public String getName() { return Name;  }
    public String getNameA() { return NameA;  }
    public String getNameB() { return NameB;  }
    public String getNameC() { return NameC;  }
    public boolean getFound() { return Found; }

    public void setNotify(boolean notify) { Notify = notify; }
    public void setA(double A) { this.A = A; }
    public void setB(double B) { this.B = B; }
    public void setC(double C) { this.C = C; }
    public void setFoud(boolean foud) { Found = foud; }

    public boolean HaveA() { return NameA.length() != 0; }
    public boolean HaveB() { return NameB.length() != 0; }
    public boolean HaveC() { return NameC.length() != 0; }
}
