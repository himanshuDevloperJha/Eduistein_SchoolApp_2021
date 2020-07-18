package com.cygnus.model;

public class Chapternumbers {
    String name,title,url;

    public Chapternumbers(String name, String title,String url) {
        this.setName(name);
        this.setTitle(title);
        this.setUrl(url);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
