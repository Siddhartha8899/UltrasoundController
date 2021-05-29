package com.example.ultrasoundcontroller.Interface;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultrasoundcontroller.MainActivity;
import com.example.ultrasoundcontroller.R;
import com.example.ultrasoundcontroller.SuperNode;

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

            /* The name of the directory, only 27 characters visible. */
            String name = child_directory.nameOfDirectory;
            if(name.length() > 27) {
                name =  name.substring(0,27) + "...";
            }
            holder._name.setText(name);

            /* Deciding which icon to put. */
            if(child_directory.type.equals("Folder")) {
                holder._image.setImageResource(R.drawable.ic_f);
            } else {
                holder._image.setImageResource(R.drawable.ic_play);
            }

            /* If directory is selection then it will change the background color of the directory to "#A52A2A" */
            if( ((MainActivity) mCtx).selected_directories.containsKey(child_directory.directoryInode)) {
                holder._image.setBackgroundColor(Color.parseColor("#A52A2A"));
                holder._edit_delete_section.setBackgroundColor(Color.parseColor("#A52A2A"));
            } else {
                holder._image.setBackgroundColor(Color.parseColor("#000000"));
                holder._edit_delete_section.setBackgroundColor(Color.parseColor("#000000"));
            }

            /* When the image is clicked, the directory is opened. */
            holder._image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) mCtx).tileClick(position);
                }
            });

            /* When the text is clicked, the rename window is opened. */
            holder._edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) mCtx).editTile(position);
                }
            });

            /* When the delete button is clicked, the directory is deleted. */
            holder._delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) mCtx).deleteTile(position);
                }
            });

            /* When the SMALL BUTTON is clicked, the directory is selected. */
            holder._click.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!((MainActivity) mCtx).rootDirectory.equals("Simulations"))
                            ((MainActivity) mCtx).tileSelected(position);
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
        Button _delete, _click;
        RelativeLayout _edit_delete_section;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            _name = itemView.findViewById(R.id.name);
            _image = itemView.findViewById(R.id.image);
            _edit = itemView.findViewById(R.id.edit);
            _delete = itemView.findViewById(R.id.delete);
            _edit_delete_section = itemView.findViewById(R.id.edit_delete_section);
            _click = itemView.findViewById(R.id.click);

        }
    }


}
