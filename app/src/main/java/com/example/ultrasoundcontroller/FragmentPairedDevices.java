package com.example.ultrasoundcontroller;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class FragmentPairedDevices extends DialogFragment {
    public MainActivity main;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Device: ");
        String[] strings = MyApplication.getApplication().getPairedDevices();

        builder.setItems(strings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyApplication.getApplication().setClient(which);

                Toast.makeText(getActivity(), "connecting", Toast.LENGTH_SHORT).show();
                while (MyApplication.getApplication().isSocketConnected() == MyApplication.getApplication().CONNECTING) {
                }

                if (MyApplication.getApplication().isSocketConnected() == MyApplication.getApplication().CONNECTION_SUCCEEDED) {
                    main = (MainActivity) getActivity();
                    main.bluetoothImage.setImageResource(R.drawable.ic_baseline_bluetooth_pressed_24);
                    Toast.makeText(getActivity(), "connected", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Failed to connect to the selected device", Toast.LENGTH_LONG).show();
                }
            }
        });




        return builder.create();
    }



}
