package com.cygnus.chatstaff;

public class Chatmodel {
    String message,user,date,time;
    int type;

    public Chatmodel(String message, String user,String date,String time,int type) {
       this.setMessage(message);
       this.setUser(user);
       this.setType(type);
       this.setDate(date);
       this.setTime(time);
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
