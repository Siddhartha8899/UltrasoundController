package com.example.ultrasoundcontroller.Interface;

import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
            String name = superNode.hashMap.get(directory.childDirectories.get(position)).nameOfDirectory;
            if(name.length() > 5) {
                name =  name.substring(0,5) + "...";
            }
            holder._name.setText(name);


            if(superNode.hashMap.get(directory.childDirectories.get(position)).type.equals("Folder")) {
                holder._image.setImageResource(R.mipmap.ic_folder);
            } else {
                holder._image.setImageResource(R.mipmap.ic_play);
            }

            holder._image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) v.getContext()).tileClick(position);
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
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            _name = itemView.findViewById(R.id.name);
            _image = itemView.findViewById(R.id.image);
            _edit = itemView.findViewById(R.id.edit);
            _delete = itemView.findViewById(R.id.delete);

        }
    }


}
