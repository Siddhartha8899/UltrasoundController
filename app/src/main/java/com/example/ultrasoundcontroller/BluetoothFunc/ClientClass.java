package com.example.ultrasoundcontroller.BluetoothFunc;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.example.ultrasoundcontroller.MyApplication;

import java.io.IOException;
import java.util.UUID;

public class ClientClass extends Thread {
    private BluetoothDevice device;
    BluetoothSocket socket;
    private static final UUID MY_UUID=UUID.fromString("8ce255c0-223a-11e0-ac64-0803450c9a66");

    SendReceive sendReceive;
    int isConnected;

    public ClientClass (BluetoothDevice device1)
    {
        device=device1;
        isConnected = MyApplication.getApplication().CONNECTING;
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
            isConnected = MyApplication.getApplication().CONNECTION_SUCCEEDED;
            sendReceive=new SendReceive(socket);

        } catch (IOException e) {
            isConnected = MyApplication.getApplication().CONNECTION_FAILED;
            e.printStackTrace();
        }
    }

    public SendReceive getSendReceive() {
        return  sendReceive;
    }

    public int getConnectionStatus() {
        return isConnected;
    }
}