package com.cygnus.model;

public class Chapter {
    String chapterno,chaptername,chapterurl,chaptertitle;

    public Chapter(String chapterno, String chaptername,String chapterurl,String chaptertitle) {
       this.setChapterno(chapterno);
       this.setChaptername(chaptername);
       this.setChapterurl(chapterurl);
       this.setChaptertitle(chaptertitle);

    }

    public String getChapterno() {
        return chapterno;
    }

    public void setChapterno(String chapterno) {
        this.chapterno = chapterno;
    }

    public String getChaptername() {
        return chaptername;
    }

    public void setChaptername(String chaptername) {
        this.chaptername = chaptername;
    }

    public String getChapterurl() {
        return chapterurl;
    }

    public void setChapterurl(String chapterurl) {
        this.chapterurl = chapterurl;
    }

    public String getChaptertitle() {
        return chaptertitle;
    }

    public void setChaptertitle(String chaptertitle) {
        this.chaptertitle = chaptertitle;
    }
}
