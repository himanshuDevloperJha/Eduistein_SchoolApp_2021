package com.cygnus.model;

public class YoutubeVideos {
    String email,cls,subject;

    public YoutubeVideos(String email, String cls, String subject) {
        this.setEmail(email);
        this.setCls(cls);
        this.setSubject(subject);


    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCls() {
        return cls;
    }

    public void setCls(String cls) {
        this.cls = cls;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }


}
