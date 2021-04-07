package com.cygnus.model;

public class StoreNotifications {
    String username,classId,message,status;

    public StoreNotifications(String username,String classId, String message,String status) {
      this.setClassId(classId);
      this.setMessage(message);
      this.setStatus(status);
      this.setUsername(username);
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
