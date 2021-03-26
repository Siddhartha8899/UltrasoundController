package com.example.ultrasoundcontroller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ultrasoundcontroller.BluetoothFunc.FragmentPairedDevices;
import com.example.ultrasoundcontroller.Interface.Directory;
import com.example.ultrasoundcontroller.Interface.GridViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    public FloatingActionButton bluetoothImage;
    public Button menu, back, add;
    public TextView nameOfDirectory;
    RecyclerView rv;
    Directory rootDirectory;
    GridViewAdapter gridViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bluetoothImage = findViewById(R.id.bluetoothImage);
        menu = findViewById(R.id.menu);
        back = findViewById(R.id.back);
        add = findViewById(R.id.add);
        rv = findViewById(R.id.organs);
        nameOfDirectory = findViewById(R.id.name_of_directory);

        enableBluetooth();
        allListeners();
        createRecyclerView();
    }

    private void createRecyclerView() {
        int images[] = { R.mipmap.ic_heart, R.mipmap.ic_heart, R.mipmap.ic_heart, R.mipmap.ic_heart, R.mipmap.ic_heart, R.mipmap.ic_heart, R.mipmap.ic_heart, R.mipmap.ic_heart};
        String names[] = {"Heart","Heart","Heart","Heart","Heart","Heart","Heart","Heart"};
        String text = "Organs";
        rootDirectory= new Directory(0, -1, text, images, names);
        gridViewAdapter = new GridViewAdapter(MainActivity.this, rootDirectory);
        rv.setAdapter(gridViewAdapter);
    }

    private void allListeners() {


        bluetoothImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!MyApplication.getApplication().isBluetoothEnabled()) {
                    Toast.makeText(getApplicationContext(),"Please turn the Bluetooth on", Toast.LENGTH_SHORT).show();
                } else {
                    FragmentPairedDevices pairedDevice = new FragmentPairedDevices();
                    pairedDevice.show(getSupportFragmentManager(), "");
                }
            }
        });


        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.START);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(gridViewAdapter.directory.parentDirectoryInode == 0) {
                    reloadRecyclerView(MyApplication.getApplication().getSuperNode().hashMap.get(0));
                    nameOfDirectory.setText(gridViewAdapter.directory.nameOfDirectory);
                    menu.setVisibility(View.VISIBLE);
                    back.setVisibility(View.INVISIBLE);
                    add.setVisibility(View.INVISIBLE);


                }
            }
        });

    }


    @Override
    protected void onDestroy() {
        restartSimulator();
        super.onDestroy();
    }

    public void restartSimulator() {
        if(MyApplication.getApplication().clientClass != null) {
            String s = "restart simulator";
            MyApplication.getApplication().getSendReceive().write(s.getBytes());
        }
    }

    private void enableBluetooth() {
        if(!MyApplication.getApplication().isBluetoothEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, 1);
        }
    }

    public void reloadRecyclerView(Directory d) {
        rootDirectory.change(d);
        gridViewAdapter.notifyDataSetChanged();
        MyApplication.getApplication().getSuperNode();
    }

}

//if(MyApplication.getApplication().clientClass != null) {
//        String s = "Heart " + strings[which];
//        MyApplication.getApplication().getSendReceive().write(s.getBytes());
//        } else {
//        Toast.makeText(mCtx,"Not connected to the Simulator", Toast.LENGTH_SHORT).show();
//        }