package com.example.ultrasoundcontroller.Interface;

import com.example.ultrasoundcontroller.MyApplication;

import java.security.acl.AclNotFoundException;
import java.util.Vector;

public class Directory {
    public static final int ROOT_DIRECTORY = 0;
    public Vector<Integer> images;
    public Vector<String> names;
    public int currentDirectoryInode;
    public int parentDirectoryInode;
    public String nameOfDirectory;
    Vector<Integer> inodes;
    Vector<String> type;

    public Directory(int currentDirectoryInode, int parentDirectoryInode, String nameOfDirectory, int[] images, String[] names) {
        this.currentDirectoryInode = currentDirectoryInode;
        this.parentDirectoryInode = parentDirectoryInode;
        this.nameOfDirectory = nameOfDirectory;
        this.images = new Vector<Integer>();
        this.names = new Vector<String>();
        this.inodes = new Vector<Integer>();
        this.type = new Vector<String>();

//        if(currentDirectoryInode == ROOT_DIRECTORY) {
            for (int i = 0; i < names.length; i++) {
                this.images.add(images[i]);
                this.names.add(names[i]);
                this.inodes.add(0);
                this.type.add("");
//            }
        }
        MyApplication.getApplication().getSuperNode().hashMap.put(currentDirectoryInode,new Directory(this));
        MyApplication.getApplication().getSuperNode().total_inode++;
    }

    public Directory(Directory d) {
        this.currentDirectoryInode = d.currentDirectoryInode;
        this.parentDirectoryInode = d.parentDirectoryInode;
        this.nameOfDirectory = d.nameOfDirectory;

        this.images = new Vector<Integer>();
        this.names = new Vector<String>();
        this.inodes = new Vector<Integer>();
        this.type = new Vector<String>();

        for (int i = 0; i < d.names.size(); i++) {
            this.images.add(d.images.get(i));
            this.names.add(d.names.get(i));
            this.inodes.add(d.inodes.get(i));
            this.type.add(d.type.get(i));
        }
    }

    public void change(Directory d) {
        this.currentDirectoryInode = d.currentDirectoryInode;
        this.parentDirectoryInode = d.parentDirectoryInode;
        this.nameOfDirectory = d.nameOfDirectory;

        this.images.clear();
        this.names.clear();
        this.inodes.clear();
        this.type.clear();
        for (int i = 0; i < d.names.size(); i++) {
            this.images.add(d.images.get(i));
            this.names.add(d.names.get(i));
            this.inodes.add(d.inodes.get(i));
            this.type.add(d.type.get(i));
        }
    }




    public int getSize() {
        return names.size();
    }


}
