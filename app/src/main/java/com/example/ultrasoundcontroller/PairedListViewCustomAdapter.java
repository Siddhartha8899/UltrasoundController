package com.example.ultrasoundcontroller;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PairedListViewCustomAdapter extends BaseAdapter {

    String[] names_of_devices;
    Context mCtxl;
    boolean clickable;

    PairedListViewCustomAdapter(Context mCtxl, String[] names_of_devices) {
        this.mCtxl = mCtxl;
        this.names_of_devices = names_of_devices;
        clickable = true;

    }

    @Override
    public int getCount() {
        return names_of_devices.length;
    }

    @Override
    public Object getItem(int position) {
        return names_of_devices[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mCtxl.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.paired_device_list_item, parent, false);
        TextView device = row.findViewById(R.id.name_of_device);
        TextView connection_status = row.findViewById(R.id.connection_status);
        device.setText(names_of_devices[position]);
        if(names_of_devices[position].equals(MyApplication.getApplication().device_connected)){
            connection_status.setText("Connected");
            row.setBackgroundColor(Color.parseColor("#4C9A2A"));
        }
        return row;
    }

    @Override
    public boolean isEnabled(int position) {
        return clickable;
    }
}
