package com.cygnus.model;

public class TeacherToken {
    String username,teacherId,token;

    public TeacherToken(String username,String teacherId, String token) {
        this.setTeacherId(teacherId);
        this.setToken(token);
        this.setUsername(username);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
