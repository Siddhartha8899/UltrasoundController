package com.example.ultrasoundcontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    ListView pairedDevicesList;
    TextView connection_status;
    SendReceive sendReceive;
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connection_status = findViewById(R.id.textView);
        pairedDevicesList = findViewById(R.id.listView);

        /* Lists all the paired devices*/
        listPairedDevices();

        /* Client socket runs after the selection is made*/
        pairedDevicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MyApplication.getApplication().setClient(i);
                while (MyApplication.getApplication().isSocketConnected() == 1) {
                }

                if (MyApplication.getApplication().isSocketConnected() == 0) {
                    Intent intent = new Intent(getApplicationContext(), Controller.class);
                    startActivity(intent);
                }
            }


        });

    }

    private void listPairedDevices() {
        String[] strings = MyApplication.getApplication().getPairedDevices();
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,strings);
        pairedDevicesList.setAdapter(arrayAdapter);
    }

}