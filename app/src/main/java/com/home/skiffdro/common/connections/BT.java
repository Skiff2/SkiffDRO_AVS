package com.home.skiffdro.common.connections;

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

public class BT implements IConnection {
    private static final int REQUEST_ENABLE_BT = 0;
    private String MacScales = "";
    public BluetoothAdapter btAdapter;

    private String BTStatus;
    private boolean isConnected;

    //Коннектимся к указанному устройству
    public static BT getInstance(String MAC) {
        if (BT.instance == null) {
            BT.instance = new BT();
        }
        if (MAC != null)
            BT.instance.Connect(MAC);
        return BT.instance;
    }

    //Получаем или инитим пустой инстанс
    public static BT getInstance() {
        if (BT.instance == null) {
            BT.instance = new BT();
        }
        return BT.instance;
    }

    @Override
    public boolean getIsConnected() {return isConnected; }

    private float valX;
    private float valY;
    private float valZ;
    private DeviceType deviceType = DeviceType.None;
    @Override
    public float getValX() {return valX; }
    @Override
    public float getValY() {return valY; }
    @Override
    public float getValZ() {return valZ; }

    @Override
    public DeviceType getDeviceType() { return deviceType; }

    public String Delim = null;

    ThreadConnected thScales;
    BTConnect ThConnectBT;

    private Timer timerReconnect;

    private List<ConnectionEvent> listeners = new ArrayList<>();

    private static BT instance;

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


        //Таймер проверки активности
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
    @Override
    public void addListener(ConnectionEvent toAdd) {
        listeners.add(toAdd);
    }

    @Override
    public String getBTStatus() {
        return BTStatus;
    }

    //Раскидывем данные заинтересованным
    void RefreshData() {
        for (ConnectionEvent hl : listeners) {
            try {
                hl.RefreshData();
            }
            catch(Exception ex) {
                listeners.remove(hl);  //Скажем "Нет" некромантии!!
                return;
            }
        }
    }

    @Override
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
                RefreshData();
                try {
                    bluetoothSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            if (success) {  // Если законнектились, тогда запускаем поток приёма и отправки данных
                isConnected = true;
                RefreshData();
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
                            if (sbprint.indexOf('|') > -1) { Delim = "\\|"; deviceType = DeviceType.Lathe; }
                            if (sbprint.indexOf('*') > -1) { Delim = "\\*"; deviceType = DeviceType.Milling; }
                        }
                        if (deviceType == DeviceType.Lathe) {
                            String[] ret = sbprint.split(Delim);
                            valX = Float.parseFloat(ret[0]) / 100;
                            valY = Float.parseFloat(ret[1]) / 200;
                            BTStatus = sbprint + "-" + ret[0] + "-" + ret[1] + '+';
                            RefreshData();
                        }
                        if (deviceType == DeviceType.Milling) {
                            String[] ret = sbprint.split(Delim);
                            valX = Float.parseFloat(ret[0]) / 200;
                            valY = Float.parseFloat(ret[1]) / 200;
                            valZ = Float.parseFloat(ret[2]) / 200;
                            RefreshData();
                        }
                    }
                } catch (IOException e) {
                    isConnected = false;
                    RefreshData();
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