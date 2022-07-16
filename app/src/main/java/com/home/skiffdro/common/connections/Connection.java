package com.home.skiffdro.common.connections;

import android.content.Context;

public class Connection implements IConnection{
    static Object instance = null;
    static Connection myInst = null;

    public static Connection Init()
    {
        if (myInst == null) myInst = new Connection();
        return myInst;
    }

    //Получаем текущий инстанс
    public static Object getInstance() {
        return Connection.instance;
    }

    //Устанавливаем текущий инстанс
    public static void setInstance(Object instance) {
        Connection.instance = instance;
    }

    @Override
    public boolean getIsConnected() {
        return ((IConnection)instance).getIsConnected();
    }

    @Override
    public float getValX() {
        return ((IConnection)instance).getValX();
    }

    @Override
    public float getValY() {
        return ((IConnection)instance).getValY();
    }

    @Override
    public float getValZ() {
        return ((IConnection)instance).getValZ();
    }

    @Override
    public DeviceType getDeviceType() {
        return ((IConnection)instance).getDeviceType();
    }

    @Override
    public void addListener(ConnectionEvent toAdd) {
        ((IConnection)instance).addListener(toAdd);
    }

    @Override
    public String getBTStatus() {
        return ((IConnection)instance).getBTStatus();
    }

    @Override
    public void cancel() {
        ((IConnection)instance).cancel();
    }
}
