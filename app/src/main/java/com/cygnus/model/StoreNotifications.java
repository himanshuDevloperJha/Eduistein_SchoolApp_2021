package com.cygnus.model;

public class StoreNotifications {
    String classId,message;

    public StoreNotifications(String classId, String message) {
      this.setClassId(classId);
      this.setMessage(message);
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
}
