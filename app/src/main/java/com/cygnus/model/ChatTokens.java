package com.cygnus.model;


public class ChatTokens {
    String username, teacherId, token, groupname;

    public ChatTokens(String username, String teacherId, String token, String groupname) {
        this.setTeacherId(teacherId);
        this.setToken(token);
        this.setUsername(username);
        this.setGroupname(groupname);
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

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }
}
