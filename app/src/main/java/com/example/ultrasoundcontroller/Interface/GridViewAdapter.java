package com.example.ultrasoundcontroller.Interface;

import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
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

import java.nio.InvalidMarkException;
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
                    Directory clicked_dir;
                    Directory current_dir = MyApplication.getApplication().getSuperNode().hashMap.get(directory.currentDirectoryInode);
                    int inode_of_clicked_dir = current_dir.inodes.get(position);

                    /* If -1 then the directory hasn't been created and we have to create it. */
                    if( inode_of_clicked_dir == -1) {
                        /* Get a new Inode number. */
                        int inode = MyApplication.getApplication().getSuperNode().total_inode;

                        /* Create a new directory. */
                        clicked_dir = new Directory(inode, directory.currentDirectoryInode, current_dir.names.get(position), null, null);

                        /* Provide information to the parent directory about the new directory. */
                        current_dir.inodes.set(position, inode);
                        current_dir.type.set(position, "Dir");
                    } else{
                        clicked_dir = MyApplication.getApplication().getSuperNode().hashMap.get(inode_of_clicked_dir);
                    }

                    /* Make some layout changes. */
                    ((MainActivity) v.getContext()).nameOfDirectory.setText(clicked_dir.nameOfDirectory);
                    if(directory.currentDirectoryInode == 0) {
                        ((MainActivity) v.getContext()).menu.setVisibility(View.INVISIBLE);
                        ((MainActivity) v.getContext()).back.setVisibility(View.VISIBLE);
                        ((MainActivity) v.getContext()).add.setVisibility(View.VISIBLE);
                    }
                    ((MainActivity) v.getContext()).reloadRecyclerView(clicked_dir);


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
