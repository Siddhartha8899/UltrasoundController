package com.example.ultrasoundcontroller;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import java.io.IOException;

public class FragmentPairedDevices extends DialogFragment {

    String[] strings;
    PairedListViewCustomAdapter adapter;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        /* Get all the paired devices and store them in a string array. */
        strings = MyApplication.getApplication().getPairedDevices();

        /* Create a custom list view. */
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View pairedDevicesListView = inflater.inflate(R.layout.paired_devices_list, null);
        ListView pairedDevicesList = pairedDevicesListView.findViewById(R.id.paired_devices_list);
        adapter = new PairedListViewCustomAdapter(getContext(), strings);
        pairedDevicesList.setAdapter(adapter);

        /* Display the list view in the AlertDialog. */
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(pairedDevicesListView);

        builder.setTitle("Choose Paired Device: ");

        pairedDevicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new ConnectionAsync(view, position).execute();
            }
        });


        return builder.create();
    }

    public class ConnectionAsync extends AsyncTask<View, Integer, Integer> {

        public MainActivity main;
        TextView connection_status;
        RelativeLayout row;
        View clicked_view;
        int clicked_position;

        ConnectionAsync(View v, int p) {
            clicked_view = v;
            clicked_position = p;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            adapter.clickable = true;
        }

        @Override
        protected void onPreExecute() {
            main = (MainActivity) getActivity();
            connection_status =  clicked_view.findViewById(R.id.connection_status);
            row = clicked_view.findViewById(R.id.pared_list_item);
            adapter.clickable = false;

        }

        void connect_to_device() {
            MyApplication.getApplication().setClient(clicked_position);
            main.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    connection_status.setText("Connecting...");
                    row.setBackgroundColor(Color.parseColor("#FFC30B"));

                }
            });

            while (MyApplication.getApplication().isSocketConnected() == MyApplication.getApplication().CONNECTING) {
            }
            if (MyApplication.getApplication().isSocketConnected() == MyApplication.getApplication().CONNECTION_SUCCEEDED) {
                main.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connection_status.setText("Connected");
                        row.setBackgroundColor(Color.parseColor("#4C9A2A"));
                        ViewCompat.setBackgroundTintList(main.bluetoothImage, ColorStateList.valueOf(Color.parseColor("#4C9A2A")));
                    }
                });
                MyApplication.getApplication().device_connected = strings[clicked_position];

            } else {
                main.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connection_status.setText("Failed to connect");
                        row.setBackgroundColor(Color.parseColor("#CF0000"));
                        ViewCompat.setBackgroundTintList(main.bluetoothImage, ColorStateList.valueOf(Color.parseColor("#CF0000")));
                    }
                });
                MyApplication.getApplication().clientClass = null;
            }

        }

        void disconnect_device() {
            main.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    connection_status.setText("Disconnected");
                    row.setBackgroundColor(Color.parseColor("#CF0000"));
                    MyApplication.getApplication().device_connected = "";
                    ViewCompat.setBackgroundTintList(main.bluetoothImage, ColorStateList.valueOf(Color.parseColor("#CF0000")));

                }
            });
            main.restartSimulator();
            try {
                MyApplication.getApplication().clientClass.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            MyApplication.getApplication().clientClass = null;
        }

        @Override
        protected Integer doInBackground(View... views) {
            /* Connected to device that is clicked. */
            boolean connected_to_same_device = strings[clicked_position].equals(MyApplication.getApplication().device_connected);

            /* Not connected to any device. */
            boolean not_connected_to_any_device = MyApplication.getApplication().device_connected.equals("");

            if(not_connected_to_any_device || connected_to_same_device) {
                if (not_connected_to_any_device) {
                    connect_to_device();
                } else {
                    disconnect_device();
                }
            } else {
                main.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "Disconnect from the current device to connect to a different device! ", Toast.LENGTH_SHORT).show();
                    }
                });


                /* Ideal thing to do is to : */
                /* -     disconnect to the current device. */
                /* -     try to connect to a new clicked device. */
            }
            return 0;
        }
    }
}
