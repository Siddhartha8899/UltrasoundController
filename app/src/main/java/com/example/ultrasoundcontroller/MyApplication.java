package com.example.ultrasoundcontroller;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.example.ultrasoundcontroller.BluetoothFunc.ClientClass;
import com.example.ultrasoundcontroller.BluetoothFunc.SendReceive;

import java.util.Set;

public class MyApplication extends Application
{
    private static MyApplication sInstance;

    public static MyApplication getApplication() {
        return sInstance;
    }

    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice[] btArray;
    public ClientClass clientClass;
    public String device_connected;
    public static final int CONNECTING = 1;
    public static final int CONNECTION_SUCCEEDED = 0;
    public static final int CONNECTION_FAILED = -1;

    public void onCreate() {
        super.onCreate();
        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        clientClass = null;
        device_connected = "";
        sInstance = this;
    }

    public boolean isBluetoothEnabled() {
        if(bluetoothAdapter.isEnabled()) { return true; }
        return false;
    }

    public String[] getPairedDevices() {
        Set<BluetoothDevice> bt= bluetoothAdapter.getBondedDevices();
        String[] strings=new String[bt.size()];
        btArray=new BluetoothDevice[bt.size()];
        int index=0;

        if( bt.size()>0)
        {
            for(BluetoothDevice device : bt) {
                btArray[index] = device;
                strings[index] = device.getName();
                index++;
            }
        }
        else {
            strings = null;
        }
        return strings;
    }

    public void setClient(int i) {
        clientClass=new ClientClass(btArray[i]);
        clientClass.start();
    }

    public int isSocketConnected() {
        return clientClass.getConnectionStatus();
    }

    public SendReceive getSendReceive() {
        return clientClass.getSendReceive();
    }

}