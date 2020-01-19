package com.rfr;

import java.util.ArrayList;

public class Post {
    private int pid;
    private int uid;
    private String name;
    private String age;
    private String date;
    private String location;
    private String level;
    private String info;
    private ArrayList<Integer> members;
    private boolean isMember = false;
    private int likes_count = 0;



    public Post(int pid, int uid, String name, String age, String date, String location, String level, String info, ArrayList<Integer> members, int likes_count) {
        this.pid = pid;
        this.uid = uid;
        this.name = name;
        this.age = age;
        this.date = date;
        this.location = location;
        this.level = level;
        this.info = info;
        this.members = members;
        this.likes_count = likes_count;
    }

    public Post(int uid, String name, String age, String date, String location, String level, String info) {
        this.uid = uid;
        this.name = name;
        this.age = age;
        this.date = date;
        this.location = location;
        this.level = level;
        this.info = info;
        this.members = new ArrayList<>();
        members.add(uid);
    }

    public Post(int pid, int uid, String name) {
        this.pid = pid;
        this.uid = uid;
        this.name = name;

    }

    public int getPid() {
        return pid;
    }

    public int getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }

    public String getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public String getLevel() {
        return level;
    }

    public String getInfo() {
        return info;
    }

    public boolean isMember() {
        return isMember;
    }

    public void setMember(boolean member) {
        isMember = member;
    }
}
