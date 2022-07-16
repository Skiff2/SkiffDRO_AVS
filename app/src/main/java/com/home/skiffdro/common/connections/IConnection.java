package com.home.skiffdro.common.connections;

public interface IConnection {
    boolean getIsConnected();

    float getValX();

    float getValY();

    float getValZ();

    DeviceType getDeviceType();

    //Накручиваем подписоту =)
    void addListener(ConnectionEvent toAdd);

    String getBTStatus();

    void cancel();

    public enum DeviceType {Milling, Lathe, None}
}
