package com.cygnus.model;

public class ChapterSpinner {
    String studentname,studentRollno;

    public ChapterSpinner(String studentname, String studentRollno) {
        this.setStudentname(studentname);
        this.setStudentRollno(studentRollno);
    }

    public String getStudentname() {
        return studentname;
    }

    public void setStudentname(String studentname) {
        this.studentname = studentname;
    }

    public String getStudentRollno() {
        return studentRollno;
    }

    public void setStudentRollno(String studentRollno) {
        this.studentRollno = studentRollno;
    }
}
