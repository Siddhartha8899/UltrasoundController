package com.example.ultrasoundcontroller;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.OutputStream;

public class SendReceive extends Thread {

    private final BluetoothSocket bluetoothSocket;
    private final OutputStream outputStream;

    public SendReceive (BluetoothSocket socket)
    {
        bluetoothSocket=socket;
        OutputStream tempOut=null;

        try {
            tempOut=bluetoothSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        outputStream=tempOut;
    }
    public void write(byte[] bytes)
    {
        try {
            outputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
