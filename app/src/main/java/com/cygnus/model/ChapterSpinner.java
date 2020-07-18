package com.cygnus.model;

public class ChapterSpinner {
    String no,name;

    public ChapterSpinner(String no, String name) {
       this.setName(name);
       this.setNo(no);
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
