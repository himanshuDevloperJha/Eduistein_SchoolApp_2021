package com.cygnus.model;

public class YoutubeVideos {
    String email, cls, subject, token;

    public YoutubeVideos(String email, String cls, String subject, String token) {
        this.setEmail(email);
        this.setCls(cls);
        this.setSubject(subject);
        this.setSubject(subject);
        this.setToken(token);

    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
