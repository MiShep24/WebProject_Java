package com.rfr;

public class BlogPost {
    private int uid;
    private int pid;
    private String title;
    private String body;
    private String image;
    private int likes_count = 0;

    public void setPid(int pid) {
        this.pid = pid;
    }

    public BlogPost(int uid, int pid, String title, String body, String image, int likes_count) {
        this.uid = uid;
        this.pid = pid;
        this.title = title;
        this.body = body;
        this.image = image;
        this.likes_count = likes_count;
    }

    public BlogPost(int uid, String title, String body, String image) {
        this.uid = uid;
        this.title = title;
        this.body = body;
        this.image = image;
    }

    public BlogPost(int uid, String title, String body) {
        this.uid = uid;
        this.title = title;
        this.body = body;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getUid() {
        return uid;
    }

    public int getPid() {
        return pid;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getImage() {
        return image;
    }
}
