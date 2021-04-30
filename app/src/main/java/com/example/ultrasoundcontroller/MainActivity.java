package com.example.ultrasoundcontroller;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ultrasoundcontroller.BluetoothFunc.FragmentPairedDevices;
import com.example.ultrasoundcontroller.Interface.Directory;
import com.example.ultrasoundcontroller.Interface.GridViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    /* For debugging purposes only. */
    private static final String TAG = "MainActivity";

    /* Bluetooth Functionality */
    public FloatingActionButton bluetoothImage;

    /* Directory Functions */
    Button menu, back, add;
    TextView nameOfDirectory;
    RecyclerView rv;
    Directory directory;
    public SuperNode superNode;
    GridViewAdapter gridViewAdapter;
    Dialog addDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Bluetooth Functionality */
        bluetoothImage = findViewById(R.id.bluetoothImage);

        /* Directory Functions */
        menu = findViewById(R.id.menu);
        back = findViewById(R.id.back);
        add = findViewById(R.id.add);
        rv = findViewById(R.id.organs);
        nameOfDirectory = findViewById(R.id.name_of_directory);
        addDialog = new Dialog(this);

        /* Bluetooth Functionality */
        /* Turns the bluetooth on, if off. */
        enableBluetooth();

        /* creates or loads the root directory. */
        createRootDirectory();

        /* onClick listener on all the buttons in this function. */
        allListeners();
    }

    private void createRootDirectory() {
        String name_of_directory = "Simulations";

        /* Does not have to create a new directory if already stored on device. */
        superNode = loadData();
        if(superNode == null) {
            /* create a new root directory. */
            superNode = new SuperNode();
            directory = new Directory(0, -1,name_of_directory,null,"Folder");
            superNode.add(0,directory);
            /* Save the directory structure on the device. */
            saveData(superNode);
        } else {
            directory = superNode.hashMap.get(0);
        }
        gridViewAdapter = new GridViewAdapter(MainActivity.this, directory, superNode);
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
                Directory parent_directory = superNode.hashMap.get(directory.parentDirectory);
                reloadRecyclerView(parent_directory);
                nameOfDirectory.setText(gridViewAdapter.directory.nameOfDirectory);
                if(parent_directory.nameOfDirectory.equals("Simulations")) {
                    menu.setVisibility(View.VISIBLE);
                    back.setVisibility(View.INVISIBLE);
                }
            }
        });

        /* This button is used to create directories (Folders or Files). */
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

                /* Note: do error checking here. */
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /* Name of the directory. */
                        String name_of_dir = name.getText().toString();

                        /* Create a new directory. */
                        Directory new_directory;
                        Integer inode = superNode.generateInode;
                        if(radioGroup.getCheckedRadioButtonId() == R.id.folder) {
                            new_directory = new Directory(inode, directory.directoryInode, name_of_dir,null, "Folder");
                        } else {
                            new_directory = new Directory(inode, directory.directoryInode, name_of_dir,videoId.getText().toString(), "File");
                        }
                        superNode.add(inode,new_directory);
                        /* Save the directory structure on the device. */
                        directory.childDirectories.add(inode);
                        saveData(superNode);
                        /* Reload the view. */
                        reloadRecyclerView(directory);
                        addDialog.dismiss();
                    }
                });
                addDialog.show();
            }
        });

    }

    public void tileClick(int position) {

        Directory current_dir = directory;
        Directory clicked_dir = superNode.hashMap.get(current_dir.childDirectories.get(position));

        if(clicked_dir.type.equals("Folder")) {
            directory = clicked_dir;
            String name = directory.nameOfDirectory;
            if(name.length() > 12) {
                name = name.substring(0,12) + "...";
            }
            nameOfDirectory.setText(name);
            if (current_dir.nameOfDirectory.equals("Simulations")) {
                menu.setVisibility(View.INVISIBLE);
                back.setVisibility(View.VISIBLE);
            }
            reloadRecyclerView(directory);
        } else {
            if(MyApplication.getApplication().clientClass != null) {
                String s = clicked_dir.videoID;
                MyApplication.getApplication().getSendReceive().write(s.getBytes());
            } else {
                Toast.makeText(this,"Not connected to the Simulator", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void editTile(int position) {
        addDialog.setContentView(R.layout.edit_pop_up);

        EditText name = addDialog.findViewById(R.id.name_the_directory);
        Button btn = addDialog.findViewById(R.id.rename_directory);
        EditText videoId = addDialog.findViewById(R.id.edit_video_id);
        Directory current_dir = directory;
        Directory clicked_dir = superNode.hashMap.get(current_dir.childDirectories.get(position));

        name.setText(clicked_dir.nameOfDirectory);
        if(clicked_dir.type.equals("File")) {
            videoId.setVisibility(View.VISIBLE);
            videoId.setText(clicked_dir.videoID);
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked_dir.nameOfDirectory = name.getText().toString();
                if(clicked_dir.type.equals("File")) {
                    clicked_dir.videoID = videoId.getText().toString();
                }
            saveData(superNode);
            reloadRecyclerView(current_dir);
            addDialog.dismiss();
            }

        });


        addDialog.show();
    }

    public void deleteTile(int position) {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        Directory current_dir = directory;
                        Directory clicked_dir = superNode.hashMap.get(current_dir.childDirectories.get(position));

                        Directory tmp_dir = clicked_dir;
                        Directory currentDir;
                        int parentInode;

                        do {
                            while (tmp_dir.type.equals("Folder") && tmp_dir.childDirectories.size() != 0) {
                                tmp_dir = superNode.hashMap.get(tmp_dir.childDirectories.get(0));
                            }
                            parentInode = tmp_dir.parentDirectory;
                            currentDir = tmp_dir;
                            superNode.hashMap.remove(tmp_dir.directoryInode);
                            tmp_dir = superNode.hashMap.get(parentInode);
                            tmp_dir.childDirectories.remove(currentDir.directoryInode);
                        }while(current_dir.childDirectories.contains(clicked_dir.directoryInode));

                        saveData(superNode);
                        reloadRecyclerView(current_dir);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you wish to permanently delete the Directory?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }



    private void saveData(SuperNode superNode) {
        SharedPreferences sharedPreferences = getSharedPreferences("superNode", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(superNode);
        editor.putString("super_node", json);
        editor.apply();
    }

    private SuperNode loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("superNode", MODE_PRIVATE);
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
        directory = d;
        gridViewAdapter.superNode = superNode;
        gridViewAdapter.directory = d;
        gridViewAdapter.notifyDataSetChanged();
    }

}

