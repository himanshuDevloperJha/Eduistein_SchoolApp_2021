package com.cygnus.model;

public class QuizScreen {
    String name,status,connect;

    public QuizScreen(String name, String status,String connect) {
        this.setName(name);
        this.setStatus(status);
        this.setConnect(connect);
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
