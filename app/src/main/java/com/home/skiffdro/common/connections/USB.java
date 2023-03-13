package com.home.skiffdro.common.connections;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.widget.Toast;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.home.skiffdro.BuildConfig;
import com.home.skiffdro.MainActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class USB implements IConnection, SerialInputOutputManager.Listener{

    private static USB instance = null;
    public String Delim = null;
    private float valX;
    private float valY;
    private float valZ;
    private DeviceType deviceType = DeviceType.None;
    private List<ConnectionEvent> listeners = new ArrayList<>();
    boolean connected = false;
    UsbSerialPort usbSerialPort;
    private SerialInputOutputManager usbIoManager;

    private enum UsbPermission { Unknown, Requested, Granted, Denied }
    private static final String INTENT_ACTION_GRANT_USB = BuildConfig.APPLICATION_ID + ".GRANT_USB";
    public UsbPermission usbPermission = UsbPermission.Unknown;

    private StringBuilder sb = new StringBuilder();
    private String BTStatus;

    public static USB getInstance(Context context) {
        //if (USB.instance == null) {
        try {
            if (USB.instance != null) {
                USB.instance.disconnect();
                USB.instance = null;
            }
        }catch (Exception ex) {}
        try {
            USB.instance = new USB();
            instance.Connect(context);
        }
        catch (Exception ex)
        {
            USB.instance = null;
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
        //}
        return USB.instance;
    }

    //Получаем или инитим пустой инстанс
    public static USB getInstance() {
        return USB.instance;
    }

    private void Connect (Context context)
    {
        UsbDevice device = null;
        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        for(UsbDevice v : usbManager.getDeviceList().values())
            //if(v.getDeviceId() == deviceId)
                device = v;
        if(device == null) {
            status("connection failed: device not found", context);
            return;
        }
        UsbSerialDriver driver = UsbSerialProber.getDefaultProber().probeDevice(device);
//        if(driver == null) {
//            driver = CustomProber.getCustomProber().probeDevice(device);
//        }
        if(driver == null) {
            status("connection failed: no driver for device", context);
            return;
        }
        if(driver.getPorts().size() < 0) {
            status("connection failed: not enough ports at device", context);
            return;
        }
        usbSerialPort = driver.getPorts().get(0);
        UsbDeviceConnection usbConnection = usbManager.openDevice(driver.getDevice());
        if(usbConnection == null && usbPermission == UsbPermission.Unknown && !usbManager.hasPermission(driver.getDevice())) {
            usbPermission = UsbPermission.Requested;
            PendingIntent usbPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(INTENT_ACTION_GRANT_USB), 0);
            usbManager.requestPermission(driver.getDevice(), usbPermissionIntent);
            return;
        }

        if(usbConnection == null) {
            if (!usbManager.hasPermission(driver.getDevice()))
                status("connection failed: permission denied", context);
            else
                status("connection failed: open failed", context);
            return;
        }

        try {
            usbSerialPort.open(usbConnection);
            usbSerialPort.setParameters(115200, 8, 1, UsbSerialPort.PARITY_NONE);

            usbIoManager = new SerialInputOutputManager(usbSerialPort, this);
            usbIoManager.start();


            status("connected", context);
            connected = true;
//            controlLines.start();
        } catch (Exception e) {
            status("connection failed: " + e.getMessage(), context);
            disconnect();
        }
    }

    private void status(String txt, Context context) {
        //Toast.makeText(context, txt, Toast.LENGTH_LONG).show();
    }

    private void disconnect() {
        connected = false;
        try {
            usbSerialPort.close();
        } catch (IOException ignored) {}
        usbSerialPort = null;
    }

//    private void send(String str) {
//        if(!connected) {
//            Toast.makeText(getActivity(), "not connected", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        try {
//            byte[] data = (str + '\n').getBytes();
//            SpannableStringBuilder spn = new SpannableStringBuilder();
//            spn.append("send " + data.length + " bytes\n");
//            spn.append(HexDump.dumpHexString(data)).append("\n");
//            spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorSendText)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            receiveText.append(spn);
//            usbSerialPort.write(data, WRITE_WAIT_MILLIS);
//        } catch (Exception e) {
//            onRunError(e);
//        }
//    }

    @Override
    public boolean getIsConnected() {
        return connected;
    }

    @Override
    public float getValX() {return valX; }
    @Override
    public float getValY() {return valY; }
    @Override
    public float getValZ() {return valZ; }

    @Override
    public DeviceType getDeviceType() { return deviceType; }

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
    public void cancel() {

    }

    @Override
    public void onNewData(byte[] data) {

        try {
            sb.append(new String(data));
            int endOfLineIndex = sb.indexOf("\r\n"); // определяем конец строки
            if (endOfLineIndex > 0) {

                String sbprint = sb.substring(0, endOfLineIndex);
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


        }
        catch (Exception ex)
        {
            Delim = ex.getMessage();
            connected = false;
            RefreshData();
            Delim = null;
        }
    }

    @Override
    public void onRunError(Exception e) {

    }
}
