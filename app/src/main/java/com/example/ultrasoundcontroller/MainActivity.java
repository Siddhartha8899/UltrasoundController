package com.example.ultrasoundcontroller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
    Directory directory;
    GridViewAdapter gridViewAdapter;
    Dialog addDialog;

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
        addDialog = new Dialog(this);

        enableBluetooth();
        allListeners();
        createRecyclerView();
    }

    private void createRecyclerView() {
        int images[] = { R.mipmap.ic_heart, R.mipmap.ic_heart, R.mipmap.ic_heart, R.mipmap.ic_heart, R.mipmap.ic_heart, R.mipmap.ic_heart, R.mipmap.ic_heart, R.mipmap.ic_heart};
        String names[] = {"Heart","Heart","Heart","Heart","Heart","Heart","Heart","Heart"};
        String name_of_directory = "Organs";

        /* The root directory. */
        directory= new Directory(0, -1, name_of_directory, images, names);
        gridViewAdapter = new GridViewAdapter(MainActivity.this, directory);
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
                int parent_directory_inode = gridViewAdapter.directory.parentDirectoryInode;
                if( parent_directory_inode == 0) {
                    reloadRecyclerView(MyApplication.getApplication().getSuperNode().hashMap.get(0));
                    nameOfDirectory.setText(gridViewAdapter.directory.nameOfDirectory);
                    menu.setVisibility(View.VISIBLE);
                    back.setVisibility(View.INVISIBLE);
                    add.setVisibility(View.INVISIBLE);
                } else {
                    reloadRecyclerView(MyApplication.getApplication().getSuperNode().hashMap.get(parent_directory_inode));
                    nameOfDirectory.setText(gridViewAdapter.directory.nameOfDirectory);
                }
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addDialog.setContentView(R.layout.custom_pop_up);

                EditText name = addDialog.findViewById(R.id.name_the_directory);
                Button btn = addDialog.findViewById(R.id.create_directory);
                RadioGroup radioGroup = addDialog.findViewById(R.id.type_of_directory);

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RadioButton radioButton;
                        int images[] = {R.mipmap.ic_folder, R.mipmap.ic_play};
                        String name_of_dir = name.getText().toString();
                        radioButton = addDialog.findViewById(radioGroup.getCheckedRadioButtonId());

                        Directory clicked_dir;
                        Directory current_dir = MyApplication.getApplication().getSuperNode().hashMap.get(directory.currentDirectoryInode);

                        /* Get a new Inode number. */
                        int inode = MyApplication.getApplication().getSuperNode().total_inode;

                        /* Provide information to the parent directory about the new directory. */
                        current_dir.inodes.add(inode);
                        if(radioGroup.getCheckedRadioButtonId() == R.id.folder) {
                            current_dir.images.add(images[0]);
                            current_dir.type.add("Dir");
                        } else {
                            current_dir.images.add(images[1]);
                            current_dir.type.add("File");
                        }
                        current_dir.names.add(name_of_dir);


                        /* Create a new directory. */
                        clicked_dir = new Directory(inode, directory.currentDirectoryInode, name_of_dir, null, null);
                        reloadRecyclerView(current_dir);
                        addDialog.dismiss();
                    }
                });

                addDialog.show();


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
        directory.change(d);
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