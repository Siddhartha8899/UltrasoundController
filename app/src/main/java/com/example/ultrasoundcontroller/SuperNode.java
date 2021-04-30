package com.example.ultrasoundcontroller;

import com.example.ultrasoundcontroller.Interface.Directory;

import java.util.HashMap;

public class SuperNode {
    public int generateInode;
    public HashMap<Integer, Directory> hashMap;

    public SuperNode() {
        hashMap = new HashMap<>();
        generateInode = 0;
    }
    public void add(Integer inode, Directory dir) {
        hashMap.put(inode, dir);
        generateInode++;
    }
}