package com.home.skiffdro.common;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BT {
    private static final int REQUEST_ENABLE_BT = 0;
    private String MacScales = "";
    public BluetoothAdapter btAdapter;

    private String BTStatus;
    public String getBTStatus() {return BTStatus;}

    private boolean isConnected;
    public boolean getIsConnected() {return isConnected; }

    private float valX;
    private float valY;
    private float valZ;
    private String DeviceType = "?";
    public float getValX() {return valX; }
    public float getValY() {return valY; }
    public float getValZ() {return valZ; }
    public String getDeviceType() { return DeviceType; }

    public String Delim = null;

    ThreadConnected thScales;
    BTConnect ThConnectBT;

    private Timer timerReconnect;

    private List<BTEvent> listeners = new ArrayList<>();

    private static BT instance;

    //Коннектимся к указанному устройству
    public static synchronized BT getInstance(String MAC) {
        if (instance == null) {
            instance = new BT();
        }
        if (MAC != null)
            instance.Connect(MAC);
        return instance;
    }
    //Получаем или инитим пустой инстанс
    public static synchronized BT getInstance() {
        if (instance == null) {
            instance = new BT();
        }
        return instance;
    }

    public BT(){}

    private void Connect (String MAC)
    {
        MacScales = MAC;
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        if (btAdapter != null) {
            if (!btAdapter.isEnabled()) {
                BTStatus = "Блютус отключен!";
            }
        } else {
            BTStatus = "Блютус отсутствует!";
            return;
        }


        //Таймер проверки активности весов
        timerReconnect = new Timer();
        TimerTask timerTaskScales = new TimerTask() {

            @Override
            public void run() {
                try {
                    if (!isConnected) {
                        try {
                            ThConnectBT.cancel();
                            ThConnectBT = null;
                            thScales = null;
                        } catch (Exception e) {
                        }
                        BluetoothDevice BTScales = btAdapter.getRemoteDevice(MacScales);
                        ThConnectBT = new BTConnect(BTScales);
                        ThConnectBT.start();  // Запускаем поток для подключения Bluetooth
                    }
                } catch (Exception ex) {
                }
            }
        };
        timerReconnect.scheduleAtFixedRate(timerTaskScales, 1000, 3000);
    }

    //Накручиваем подписоту =)
    public void addListener(BTEvent toAdd) {
        listeners.add(toAdd);
    }

    //Раскидывем данные заинтересованным
    public void RefreshListeners() {
        for (BTEvent hl : listeners) {
            try {
                hl.RefreshBTData();
            }
            catch(Exception ex) {
                listeners.remove(hl);  //Скажем "Нет" некромантии!!
                return;
            }
        }
    }

    public void cancel(){
        ThConnectBT.cancel();
        ThConnectBT = null;
        thScales = null;
    }

    class BTConnect extends Thread { // Поток для коннекта с Bluetooth
        private BluetoothSocket bluetoothSocket = null;

        private BTConnect(BluetoothDevice device) {

            try {
                Method m = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                bluetoothSocket = (BluetoothSocket) m.invoke(device, 1);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() { // Коннект

            boolean success = false;

            try {
                bluetoothSocket.connect();
                success = true;
            } catch (IOException e) {
                e.printStackTrace();
                isConnected = false;
                RefreshListeners();
                try {
                    bluetoothSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            if (success) {  // Если законнектились, тогда запускаем поток приёма и отправки данных
                isConnected = true;
                RefreshListeners();
                thScales = new ThreadConnected(bluetoothSocket);
                thScales.start(); // запуск потока приёма и отправки данных
            }
        }

        public void cancel() {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    } // END ThreadConnectBTdevice:

    class ThreadConnected extends Thread {    // Поток - приём и отправка данных

        private final InputStream connectedInputStream;
        private final OutputStream connectedOutputStream;
        private StringBuilder sb = new StringBuilder();

        private String sbprint;

        public ThreadConnected(BluetoothSocket socket) {

            InputStream in = null;
            OutputStream out = null;

            try {
                in = socket.getInputStream();
                out = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            connectedInputStream = in;
            connectedOutputStream = out;
        }


        @Override
        public void run() { // Приём данных

            byte nByte = 0;
            while (true) {

                try {
                    byte[] buffer = new byte[1];
                    int bytes = connectedInputStream.read(buffer);
                    String strIncom = new String(buffer, 0, bytes);
                    sb.append(strIncom); // собираем символы в строку
                    int endOfLineIndex = sb.indexOf("\r\n"); // определяем конец строки

                    if (endOfLineIndex > 0) {

                        sbprint = sb.substring(0, endOfLineIndex);
                        sb.delete(0, sb.length());
                        if (Delim == null)
                        {
                            if (sbprint.indexOf('|') > -1) { Delim = "\\|"; DeviceType = "Токарный"; }
                            if (sbprint.indexOf('*') > -1) { Delim = "\\*"; DeviceType = "Фрезерный"; }
                        }
                        if (DeviceType.equals("Токарный")) {
                            String[] ret = sbprint.split(Delim);
                            valX = Float.parseFloat(ret[0]) / 100;
                            valY = Float.parseFloat(ret[1]) / 200;
                            RefreshListeners();
                        }
                        if (DeviceType.equals("Фрезерный")) {
                            String[] ret = sbprint.split(Delim);
                            valX = Float.parseFloat(ret[0]) / 200;
                            valY = Float.parseFloat(ret[1]) / 200;
                            valZ = Float.parseFloat(ret[2]) / 200;
                            RefreshListeners();
                        }
                    }
                } catch (IOException e) {
                    isConnected = false;
                    RefreshListeners();
                    break;
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                connectedOutputStream.write(buffer);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}