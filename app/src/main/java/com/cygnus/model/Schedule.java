package com.cygnus.model;

public class Schedule {
    String email,classs,subject,date,starttime,endtime,zoomlink;

    public Schedule(String email, String classs, String subject, String date,
                    String starttime,String endtime,String zoomlink) {
      this.setEmail(email);
      this.setClasss(classs);
      this.setSubject(subject);
      this.setDate(date);
      this.setStarttime(starttime);
      this.setEndtime(endtime);
      this.setZoomlink(zoomlink);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getClasss() {
        return classs;
    }

    public void setClasss(String classs) {
        this.classs = classs;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getZoomlink() {
        return zoomlink;
    }

    public void setZoomlink(String zoomlink) {
        this.zoomlink = zoomlink;
    }


}
