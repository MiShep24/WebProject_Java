package com.rfr;

public class BlogComment {
    private int id;
    private int pid;
    private int uid;
    private boolean isOrganizer;
    private boolean isMy;
    private String text;
    private String date;
    private User owner;

    public BlogComment(int id, int pid, int uid, String text, String date, User owner, BlogPost blogPost, int my_uid) {
        this.id = id;
        this.pid = pid;
        this.uid = uid;
        this.text = text;
        this.date = date;
        this.owner = owner;
        this.isOrganizer = blogPost.getUid() == uid;
        this.isMy = uid == my_uid;
    }

    public int getId() {
        return id;
    }

    public int getPid() {
        return pid;
    }

    public int getUid() {
        return uid;
    }

    public boolean isOrganizer() {
        return isOrganizer;
    }

    public boolean isMy() {
        return isMy;
    }

    public String getText() {
        return text;
    }

    public String getDate() {
        return date;
    }

    public User getOwner() {
        return owner;
    }

    public void setMy(boolean my) {
        isMy = my;
    }
}
