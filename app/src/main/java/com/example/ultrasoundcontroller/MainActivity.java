package com.example.ultrasoundcontroller;

import androidx.annotation.NonNull;
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
import android.view.MenuItem;
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
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    /* For debugging purposes only. */
    private static final String TAG = "MainActivity";

    /* Bluetooth Functionality variables declaration */
    public FloatingActionButton bluetoothImage;

    /* Directory Functionality variables declaration */
    Button menu, back, add, copy, paste;
    TextView nameOfDirectory;
    RecyclerView rv;
    Directory directory;
    public SuperNode superNode;
    GridViewAdapter gridViewAdapter;
    Dialog addDialog;
    public String rootDirectory;
    public HashMap<Integer, Directory> selected_directories, copied_directories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Bluetooth Functionality variable definition */
        bluetoothImage = findViewById(R.id.bluetoothImage);

        /* Directory Functionality variable definition */
        menu = findViewById(R.id.menu);
        back = findViewById(R.id.back);
        add = findViewById(R.id.add);
        copy = findViewById(R.id.copy);
        paste = findViewById(R.id.paste);
        rv = findViewById(R.id.organs);
        nameOfDirectory = findViewById(R.id.name_of_directory);
        addDialog = new Dialog(this);
        rootDirectory = "Simulations";
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        selected_directories = new HashMap<>();
        copied_directories = new HashMap<>();

        /* Bluetooth Functionality */
        /* Turns the bluetooth on, if off. */
        enableBluetooth();

        /* creates or loads the root directories. */
        createRootDirectories();

        /* onClick listener on all the buttons in this function. */
        allListeners();
    }

    private void createRootDirectories() {
        String name_of_first_directory = "Simulations";
        String name_of_second_directory = "Organs";

        /* Does not have to create a new directory if the directory structure is already stored on device. */
        superNode = loadData();
        if(superNode == null) {
            /* SuperNode stores all the directories. */
            superNode = new SuperNode();

            /* Create the Organs directory. */
            directory = new Directory(1, -1,name_of_second_directory,null,"Folder");
            superNode.add(1,directory);

            /* Create the Simulations directory. */
            directory = new Directory(0, -1,name_of_first_directory,null,"Folder");
            superNode.add(0,directory);

            /* Save the directory structure on the device. */
            saveData(superNode);
        } else {
            /* If the directory structure is saved on the device, directly get the 'simulations' directory. */
            directory = superNode.hashMap.get(0);
        }

        /* Represent the 'Simulations' directory on the device. */
        gridViewAdapter = new GridViewAdapter(this, directory, superNode);
        rv.setAdapter(gridViewAdapter);
    }

    private void allListeners() {

        /* Shows the list of all the paired devices. */
        bluetoothImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Bluetooth should be enabled to see the paired devices list. */
                if(!MyApplication.getApplication().isBluetoothEnabled()) {
                    Toast.makeText(getApplicationContext(),"Please turn the Bluetooth on", Toast.LENGTH_SHORT).show();
                } else {
                    FragmentPairedDevices pairedDevice = new FragmentPairedDevices();
                    pairedDevice.show(getSupportFragmentManager(), "");
                }
            }
        });

        /* Clicking on the menu button open up a side navigation bar, with two directories 'Simulations' and 'Organs'. */
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.START);

            }
        });

        /* clicking on the back button changes the current directory to the parent directory. */
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Directory parent_directory = superNode.hashMap.get(directory.parentDirectory);
                reloadRecyclerView(parent_directory);
                nameOfDirectory.setText(gridViewAdapter.directory.nameOfDirectory);

                /*If the parent directory is either the 'simulations' or the 'organs' directory then the back button becomes invisible. (as it is not required) */
                if(parent_directory.parentDirectory == -1) {
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
                TextView errorText = addDialog.findViewById(R.id.error);

                /* The 'id' field becomes visible when the file radio button is selected. */
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

                /* After the 'create' button is clicked, all the details are stored and a directory is created! */
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /* Name of the directory. */
                        String name_of_dir = name.getText().toString();

                        /* Error, if name of  directory is null or the id of a video is null. */
                        if(name_of_dir.equals("") || (radioGroup.getCheckedRadioButtonId() == R.id.file && videoId.getText().toString().equals("") )) {
                            errorText.setVisibility(View.VISIBLE);
                            return;
                        }

                        /* Create a new directory. */
                        Directory new_directory;
                        Integer inode = superNode.generateInode;
                        if(radioGroup.getCheckedRadioButtonId() == R.id.folder) {
                            new_directory = new Directory(inode, directory.directoryInode, name_of_dir,null, "Folder");
                        } else {
                            new_directory = new Directory(inode, directory.directoryInode, name_of_dir,videoId.getText().toString(), "File");
                        }
                        superNode.add(inode,new_directory);

                        /* Add the new directory in the parent directory. */
                        directory.childDirectories.add(inode);

                        /* Save the directory structure on the device. */
                        saveData(superNode);

                        /* Reload the view. */
                        reloadRecyclerView(directory);
                        addDialog.dismiss();
                    }
                });
                addDialog.show();
            }
        });

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!selected_directories.isEmpty()) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                /* Directories are copied and selected directories are unselected. */
                                case DialogInterface.BUTTON_POSITIVE:
                                    Toast.makeText(v.getContext(), "Copied!", Toast.LENGTH_LONG).show();
                                    copied_directories.putAll(selected_directories);
                                    selected_directories.clear();
                                    reloadRecyclerView(directory);
                                    break;

                                /* Directories are not copied and selected directories are unselected. */
                                case DialogInterface.BUTTON_NEGATIVE:
                                    selected_directories.clear();
                                    reloadRecyclerView(directory);
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setMessage("Do you wish to copy " + selected_directories.size() + " directories?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
            }
            else {
                Toast.makeText(v.getContext(), "No directories selected!", Toast.LENGTH_LONG).show();
            }


        }
        });

        paste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (copied_directories.isEmpty()) {
                    Toast.makeText(v.getContext(), "No directories copied!", Toast.LENGTH_LONG).show();
                } else {
                    for (Directory dir : copied_directories.values()) {
                        Queue<Integer> qe=new LinkedList<Integer>();
                        qe.add(dir.directoryInode);
                        qe.add(directory.directoryInode);

                        Directory tmp_dir, parent_dir;
                        do {
                            Directory new_directory;
                            tmp_dir = superNode.hashMap.get(qe.poll());
                            parent_dir = superNode.hashMap.get(qe.poll());

                            int new_directory_inode = superNode.generateInode;

                            if(tmp_dir.type.equals("Folder")) {
                                 new_directory = new Directory(new_directory_inode, parent_dir.directoryInode, tmp_dir.nameOfDirectory, null, "Folder");
                            } else {
                                new_directory = new Directory(new_directory_inode, parent_dir.directoryInode, tmp_dir.nameOfDirectory, tmp_dir.videoID, "File");
                            }
                            parent_dir.childDirectories.add(new_directory_inode);
                            superNode.add(new_directory_inode,new_directory);

                            for(int i = 0; tmp_dir.type.equals("Folder") && i< tmp_dir.childDirectories.size();i++) {
                                qe.add(tmp_dir.childDirectories.get(i));
                                qe.add(new_directory_inode);
                            }
                        } while(!qe.isEmpty());

                    }

                    /* Save the directory structure on the device. */
                    saveData(superNode);
                    /* Reload the view. */
                    reloadRecyclerView(directory);
                    copied_directories.clear();
                }
            }
        });


    }

    public void tileClick(int position) {

        Directory current_dir = directory;
        Directory clicked_dir = superNode.hashMap.get(current_dir.childDirectories.get(position));

        /* If directory is a folder, it opens the folder. */
        if(clicked_dir.type.equals("Folder")) {
            directory = clicked_dir;
            String name = directory.nameOfDirectory;
            if(name.length() > 12) {
                name = name.substring(0,12) + "...";
            }
            nameOfDirectory.setText(name);
            if (current_dir.parentDirectory == -1) {
                menu.setVisibility(View.INVISIBLE);
                back.setVisibility(View.VISIBLE);
            }
            reloadRecyclerView(directory);
        /* If directory is a file, it sends the ID to the simulator, given the the simulator device is connected. */
        } else {
            if(MyApplication.getApplication().clientClass != null) {
                String s = clicked_dir.videoID;
                MyApplication.getApplication().getSendReceive().write(s.getBytes());

                addDialog.setContentView(R.layout.play_pop_up);
                Button play = addDialog.findViewById(R.id.play);
                Button pause = addDialog.findViewById(R.id.pause);

                play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String s = "resume";
                        MyApplication.getApplication().getSendReceive().write(s.getBytes());
                    }
                });

                pause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String s = "pause";
                        MyApplication.getApplication().getSendReceive().write(s.getBytes());
                    }
                });

                addDialog.show();


            } else {
                Toast.makeText(this,"Not connected to the Simulator", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void tileSelected(int position) {

        /* Adds the selected directories in a vector. */
        Directory current_dir = directory;
        int clicked_dir_inode = current_dir.childDirectories.get(position);
        if(selected_directories.containsKey(clicked_dir_inode)) {
            selected_directories.remove(clicked_dir_inode);
        } else {
            selected_directories.put(clicked_dir_inode, superNode.hashMap.get(clicked_dir_inode));
        }
        reloadRecyclerView(current_dir);

    }

    public void editTile(int position) {
        addDialog.setContentView(R.layout.edit_pop_up);

        EditText name = addDialog.findViewById(R.id.name_the_directory);
        Button btn = addDialog.findViewById(R.id.rename_directory);
        EditText videoId = addDialog.findViewById(R.id.edit_video_id);
        Directory current_dir = directory;
        Directory clicked_dir = superNode.hashMap.get(current_dir.childDirectories.get(position));
        TextView error = addDialog.findViewById(R.id.error);

        name.setText(clicked_dir.nameOfDirectory);

        if(clicked_dir.type.equals("File")) {
            videoId.setVisibility(View.VISIBLE);
            videoId.setText(clicked_dir.videoID);
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Error, if name of  directory is null or the id of a video is null. */
                if (name.getText().toString().equals("") || (clicked_dir.type.equals("File") && videoId.getText().toString().equals(""))) {
                    error.setVisibility(View.VISIBLE);
                    return;
                }
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
                        int clicked_directory_inode = current_dir.childDirectories.get(position);

                        Stack<Integer> depth_first = new Stack<>();
                        depth_first.push(clicked_directory_inode);
                        Directory tmp_dir;
                        int tmp_dir_inode;

                        do {
                            tmp_dir_inode = depth_first.pop();
                            tmp_dir = superNode.hashMap.get(tmp_dir_inode);
                            for (int i = 0; tmp_dir.type.equals("Folder") && i < tmp_dir.childDirectories.size(); i++) {
                                depth_first.push(tmp_dir.childDirectories.get(i));
                            }
                            superNode.hashMap.remove(tmp_dir.directoryInode);
                        } while(!depth_first.isEmpty());

                        current_dir.childDirectories.removeElement(clicked_directory_inode);
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.simulations_directory) {
            Directory clicked_dir = superNode.hashMap.get(0);
            rootDirectory = clicked_dir.nameOfDirectory;
            nameOfDirectory.setText(rootDirectory);
            menu.setVisibility(View.VISIBLE);
            back.setVisibility(View.INVISIBLE);
            paste.setVisibility(View.VISIBLE);
            copy.setVisibility(View.INVISIBLE);
            reloadRecyclerView(clicked_dir);
            rootDirectory = "Simulations";

        } else if (id == R.id.organ_directory) {
            Directory clicked_dir = superNode.hashMap.get(1);
            rootDirectory = clicked_dir.nameOfDirectory;
            nameOfDirectory.setText(rootDirectory);
            menu.setVisibility(View.VISIBLE);
            back.setVisibility(View.INVISIBLE);
            paste.setVisibility(View.INVISIBLE);
            copy.setVisibility(View.VISIBLE);
            reloadRecyclerView(clicked_dir);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(directory.parentDirectory != -1) {
            Directory parent_directory = superNode.hashMap.get(directory.parentDirectory);
            reloadRecyclerView(parent_directory);
            nameOfDirectory.setText(gridViewAdapter.directory.nameOfDirectory);
            if(parent_directory.parentDirectory == -1) {
                menu.setVisibility(View.VISIBLE);
                back.setVisibility(View.INVISIBLE);
            }
        } else {
            super.onBackPressed();
        }

    }
}

