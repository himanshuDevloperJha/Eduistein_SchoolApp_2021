package com.cygnus.model;

public class QuizScreen {
    String name,status,connect,classid;
    Integer quizindex;

    public QuizScreen(String name,String classid, String status,String connect,Integer quizindex) {
        this.setName(name);
        this.setStatus(status);
        this.setConnect(connect);
        this.setQuizindex(quizindex);
        this.setClassid(classid);
    }

    public String getClassid() {
        return classid;
    }

    public void setClassid(String classid) {
        this.classid = classid;
    }

    public Integer getQuizindex() {
        return quizindex;
    }

    public void setQuizindex(Integer quizindex) {
        this.quizindex = quizindex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getConnect() {
        return connect;
    }

    public void setConnect(String connect) {
        this.connect = connect;
    }
}
