package com.example.ultrasoundcontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Controller extends AppCompatActivity {

    SendReceive sendReceive;
    BluetoothSocket bluetoothSocket;
    EditText sendingtext;
    Button send;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);

        sendingtext = findViewById(R.id.editText);
        send = findViewById(R.id.button);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string = sendingtext.getText().toString();
                MyApplication.getApplication().getSendReceive().write(string.getBytes());
            }
        });



    }
}