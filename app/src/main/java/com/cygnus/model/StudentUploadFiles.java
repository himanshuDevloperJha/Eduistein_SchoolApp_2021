package com.cygnus.model;

public class StudentUploadFiles {
    String classId,teacherId,subject,studentname;

    public StudentUploadFiles(String classId, String teacherId, String subject, String studentname) {
        this.setClassId(classId);
        this.setTeacherId(teacherId);
        this.setSubject(subject);
        this.setStudentname(studentname);
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getStudentname() {
        return studentname;
    }

    public void setStudentname(String studentname) {
        this.studentname = studentname;
    }
}
