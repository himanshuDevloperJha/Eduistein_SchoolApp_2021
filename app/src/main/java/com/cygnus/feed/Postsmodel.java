package com.cygnus.feed;

public class Postsmodel {
    String description,username,urlpath, userclassid,filename, postdate,key;

    public Postsmodel(String description, String username, String urlpath,String userclassid,String filename,String postdate,String key) {
      this.setDescription(description);
      this.setUsername(username);
      this.setUrlpath(urlpath);
      this.setPostdate(postdate);
      this.setKey(key);
      this.setUserclassId(userclassid);
      this.setFilename(filename);

    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getUserclassId() {
        return userclassid;
    }

    public void setUserclassId(String userclassid) {
        this.userclassid = userclassid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPostdate() {
        return postdate;
    }

    public void setPostdate(String postdate) {
        this.postdate = postdate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUrlpath() {
        return urlpath;
    }

    public void setUrlpath(String urlpath) {
        this.urlpath = urlpath;
    }
}
