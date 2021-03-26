package com.example.ultrasoundcontroller.Interface;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultrasoundcontroller.Controller;
import com.example.ultrasoundcontroller.MainActivity;
import com.example.ultrasoundcontroller.MyApplication;
import com.example.ultrasoundcontroller.R;

import java.util.ArrayList;

public class GridViewAdapter extends RecyclerView.Adapter<GridViewAdapter.MyViewHolder> {

    Context mCtx;
    public Directory directory;

    public GridViewAdapter(Context mCtx, Directory directory) {
        this.mCtx = mCtx;
        this.directory = directory;
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
            holder._name.setText(directory.names.get(position));
            holder._image.setImageResource(directory.images.get(position));

            holder._linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Directory d;
                    if(MyApplication.getApplication().getSuperNode().hashMap.get(directory.currentDirectoryInode).inodes.get(position) == 0) {
                        int inode = MyApplication.getApplication().getSuperNode().total_inode;
                        int images[] = { R.mipmap.ic_folder};
                        String names[] = {"Left View"};
                        d = new Directory(inode, directory.currentDirectoryInode, directory.names.get(position), images, names);
                        MyApplication.getApplication().getSuperNode().hashMap.get(directory.currentDirectoryInode).inodes.set(position, inode);
                        MyApplication.getApplication().getSuperNode().hashMap.get(directory.currentDirectoryInode).type.set(position, "Dir");
                    } else{
                        d = MyApplication.getApplication().getSuperNode().hashMap.get(directory.inodes.get(position) );
                    }

                    ((MainActivity) v.getContext()).nameOfDirectory.setText(directory.names.get(position));
                    if(directory.currentDirectoryInode == 0) {
                        ((MainActivity) v.getContext()).menu.setVisibility(View.INVISIBLE);
                        ((MainActivity) v.getContext()).back.setVisibility(View.VISIBLE);
                        ((MainActivity) v.getContext()).add.setVisibility(View.VISIBLE);
                    }
                    ((MainActivity) v.getContext()).reloadRecyclerView(d);


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
        LinearLayout _linearLayout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            _name = itemView.findViewById(R.id.name);
            _image = itemView.findViewById(R.id.image);
            _linearLayout = itemView.findViewById(R.id.item);

        }
    }
}
