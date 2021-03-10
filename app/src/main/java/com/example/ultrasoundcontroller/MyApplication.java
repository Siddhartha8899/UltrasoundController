package com.example.ultrasoundcontroller;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
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
    private static final UUID MY_UUID=UUID.fromString("8ce255c0-223a-11e0-ac64-0803450c9a66");

    public static MyApplication getApplication() {
        return sInstance;
    }

    SendReceive sendReceive;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice[] btArray;
    BluetoothSocket btSocket = null;
    ClientClass clientClass;
    int isConnected;


    public void onCreate() {
        super.onCreate();
        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        sInstance = this;
    }

    public String[] getPairedDevices() {
        Set<BluetoothDevice> bt= bluetoothAdapter.getBondedDevices();
        String[] strings=new String[bt.size()];
        btArray=new BluetoothDevice[bt.size()];
        int index=0;
        if(bt.size() == 0) {
            Toast.makeText(getApplicationContext(), "Where is ", Toast.LENGTH_SHORT).show();
        }
        if( bt.size()>0)
        {
            for(BluetoothDevice device : bt) {
                btArray[index] = device;
                strings[index] = device.getName();
                index++;
            }
        }
        return strings;
    }

    public void setClient(int i) {
        clientClass=new ClientClass(btArray[i]);
        clientClass.start();
    }

    public int isSocketConnected() {
        return isConnected;
    }

    public SendReceive getSendReceive() {
        return sendReceive;
    }

    public class ClientClass extends Thread {
        private BluetoothDevice device;
        BluetoothSocket socket;

        public ClientClass (BluetoothDevice device1)
        {
            device=device1;
            isConnected = 1;
            try {
                socket=device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run()
        {
            try {
                socket.connect();
                isConnected = 0;
                sendReceive=new SendReceive(socket);
                sendReceive.start();

            } catch (IOException e) {
                isConnected = -1;
                e.printStackTrace();
            }
        }
    }
}