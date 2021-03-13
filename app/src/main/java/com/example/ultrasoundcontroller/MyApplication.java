package com.example.ultrasoundcontroller;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.IOException;
import java.net.Socket;
import java.util.Set;
import java.util.UUID;

public class MyApplication extends Application
{
    private static MyApplication sInstance;

    public static MyApplication getApplication() {
        return sInstance;
    }

    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice[] btArray;
    ClientClass clientClass;
    static final int CONNECTING = 1;
    static final int CONNECTION_SUCCEEDED = 0;
    static final int CONNECTION_FAILED = -1;

    public void onCreate() {
        super.onCreate();
        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        clientClass = null;
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