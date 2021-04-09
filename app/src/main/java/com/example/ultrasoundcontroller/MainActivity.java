package com.example.ultrasoundcontroller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.ultrasoundcontroller.Interface.SuperNode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

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
        SuperNode sNode = loadData();
        if(sNode == null) {
            MyApplication.getApplication().superNode = new SuperNode();
        } else {
            MyApplication.getApplication().superNode = sNode;

        }
        createRecyclerView();
    }

    private void createRecyclerView() {
        String name_of_directory = "Organs";

        /* The root directory. */
        if(!MyApplication.getApplication().getSuperNode().hashMap.isEmpty()) {
            directory = new Directory(MyApplication.getApplication().getSuperNode().hashMap.get(0));
        } else {
            directory = new Directory(0, -1, name_of_directory, null, null);
            saveData(MyApplication.getApplication().getSuperNode());
        }
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
                EditText videoId = addDialog.findViewById(R.id.video_id);

                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if(checkedId == R.id.folder) {
                            videoId.setVisibility(View.INVISIBLE);
                        } else {
                            videoId.setVisibility(View.VISIBLE);
                        }
                    }
                });

                if(radioGroup.getCheckedRadioButtonId() == R.id.folder) {
                    videoId.setVisibility(View.INVISIBLE);
                } else {
                    videoId.setVisibility(View.VISIBLE);
                }

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int images[] = {R.mipmap.ic_folder, R.mipmap.ic_play};
                        String name_of_dir = name.getText().toString();

                        Directory clicked_dir;
                        Directory current_dir = MyApplication.getApplication().getSuperNode().hashMap.get(directory.currentDirectoryInode);

                        /* Get a new Inode number. */
                        int inode = MyApplication.getApplication().getSuperNode().total_inode;

                        /* Create a new directory. */
                        clicked_dir = new Directory(inode, directory.currentDirectoryInode, name_of_dir, null, null);

                        /* Provide information to the parent directory about the new directory. */
                        current_dir.inodes.add(inode);
                        if(radioGroup.getCheckedRadioButtonId() == R.id.folder) {
                            current_dir.images.add(images[0]);
                            current_dir.type.add("Dir");
                        } else {
                            current_dir.images.add(images[1]);
                            current_dir.type.add("File");
                            MyApplication.getApplication().getSuperNode().hashMap.get(inode).videoID = videoId.getText().toString();
                        }
                        current_dir.names.add(name_of_dir);




                        /* Store the key value pair on the device. */
//                        saveData(MyApplication.getApplication().getSuperNode());

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

    private void saveData(SuperNode supernode) {
        SharedPreferences sharedPreferences = getSharedPreferences("hashMap", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(supernode);
        editor.putString("super_node", json);
        editor.apply();
    }

    private SuperNode loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("hashMap", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("super_node", null);
        Type type = new TypeToken<SuperNode>() {}.getType();
        return gson.fromJson(json,type);
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
    }

    public void onLongTileClick() {
        addDialog.setContentView(R.layout.custom_pop_up);

        EditText name = addDialog.findViewById(R.id.name_the_directory);
        Button btn = addDialog.findViewById(R.id.create_directory);
        RadioGroup radioGroup = addDialog.findViewById(R.id.type_of_directory);
    }

}

