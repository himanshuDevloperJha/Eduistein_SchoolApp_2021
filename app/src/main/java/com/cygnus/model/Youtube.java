package com.cygnus.model;

public class Youtube {
    String email,subject,cls;

    public Youtube(String email, String subject,String cls) {
       this.setEmail(email);
       this.setSubject(subject);
       this.setCls(cls);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCls() {
        return cls;
    }

    public void setCls(String cls) {
        this.cls = cls;
    }

}
