package com.example.ultrasoundcontroller.Interface;

import java.util.HashMap;

public class SuperNode {
    public int total_inode;
    public HashMap<Integer, Directory> hashMap;

    public SuperNode() {
        hashMap = new HashMap<>();
        total_inode = 0;
    }

}
