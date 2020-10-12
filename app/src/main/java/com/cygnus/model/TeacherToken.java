package com.cygnus.model;

public class TeacherToken {
    String teacherId,token;

    public TeacherToken(String teacherId, String token) {
        this.setTeacherId(teacherId);
        this.setToken(token);
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
