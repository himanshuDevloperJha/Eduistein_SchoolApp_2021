package com.cygnus.feed;

public class LikePostsmodel {
    String username,postname,filename;

    public LikePostsmodel(String username, String postname,String filename) {
        this.setUsername(username);
        this.setPostname(postname);
        this.setFilename(filename);
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPostname() {
        return postname;
    }

    public void setPostname(String postname) {
        this.postname = postname;
    }
}
