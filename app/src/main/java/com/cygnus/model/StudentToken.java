package com.cygnus.model;

public class StudentToken {
    String classId,token;

    public StudentToken(String classId, String token) {
        this.setClassId(classId);
        this.setToken(token);
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
