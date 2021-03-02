package com.cygnus.model;

public class StudentToken {
    String username,classId,token;

    public StudentToken(String username,String classId, String token) {
        this.setClassId(classId);
        this.setToken(token);
        this.setUsername(username);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
