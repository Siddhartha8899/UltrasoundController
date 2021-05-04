package com.example.ultrasoundcontroller.Interface;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultrasoundcontroller.Controller;
import com.example.ultrasoundcontroller.MainActivity;
import com.example.ultrasoundcontroller.MyApplication;
import com.example.ultrasoundcontroller.R;
import com.example.ultrasoundcontroller.SuperNode;

import java.nio.InvalidMarkException;
import java.util.ArrayList;

public class GridViewAdapter extends RecyclerView.Adapter<GridViewAdapter.MyViewHolder> {

    Context mCtx;
    public Directory directory;
    public SuperNode superNode;

    public GridViewAdapter(Context mCtx, Directory directory, SuperNode superNode) {
        this.mCtx = mCtx;
        this.directory = directory;
        this.superNode = superNode;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.item_card, parent,false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Directory child_directory = superNode.hashMap.get(directory.childDirectories.get(position));
            String name = child_directory.nameOfDirectory;
            if(name.length() > 27) {
                name =  name.substring(0,27) + "...";
            }
            holder._name.setText(name);

            if(child_directory.type.equals("Folder")) {
                holder._image.setImageResource(R.drawable.ic_f);
            } else {
                holder._image.setImageResource(R.drawable.ic_play);
            }

            if(!((MainActivity) holder.itemView.getContext()).rootDirectory.equals("Simulations")) {
                holder.select.setVisibility(View.VISIBLE);
            }

            if(((MainActivity) holder.itemView.getContext()).selected_directories.containsKey(child_directory.directoryInode)) {
                holder.select.setChecked(true);

            } else {
                holder.select.setChecked(false);

            }


            holder._image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) v.getContext()).tileClick(position);
                }
            });

            holder.select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!((MainActivity) v.getContext()).rootDirectory.equals("Simulations")) {
                        ((MainActivity) v.getContext()).checkBoxClick(position);
                    }
                }
            });

            holder._edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) v.getContext()).editTile(position);
                }
            });

            holder._delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) v.getContext()).deleteTile(position);
                }
            });

    }

    @Override
    public int getItemCount() {
        return directory.getSize();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView _name;
        ImageView _image;
        RelativeLayout _edit;
        Button _delete;
        RelativeLayout _edit_delete_section;
        CheckBox select;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            _name = itemView.findViewById(R.id.name);
            _image = itemView.findViewById(R.id.image);
            _edit = itemView.findViewById(R.id.edit);
            _delete = itemView.findViewById(R.id.delete);
            _edit_delete_section = itemView.findViewById(R.id.edit_delete_section);
            select = itemView.findViewById(R.id.select);

        }
    }


}
