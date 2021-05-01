package com.example.ultrasoundcontroller.Interface;

import com.example.ultrasoundcontroller.MyApplication;

import java.security.acl.AclNotFoundException;
import java.util.Vector;

/* A directory can be a folder or a file. */
public class Directory {

    /* Directory contents. */
    public Vector<Integer> childDirectories;

    /* Info about the directory.*/
    public Integer parentDirectory;
    public Integer directoryInode;
    public String nameOfDirectory;
    public String videoID;
    public String type;

    public Directory(Integer directoryInode, Integer parentDirectory, String nameOfDirectory, String videoID, String type) {
        this.directoryInode = directoryInode;
        this.parentDirectory = parentDirectory;
        this.nameOfDirectory = nameOfDirectory;
        this.videoID = videoID;
        this.type = type;

        if(type.equals("Folder")) {
            this.childDirectories = new Vector<Integer>();
        }
    }

    public int getSize() {
        return childDirectories.size();
    }


}
