package com.cygnus.model;

public class QuizModel {
    String questno, description, a, b, c, d, answer;

    public QuizModel(String questno, String description, String a,
                     String b, String c,
                     String d, String answer) {
        this.setQuestno(questno);
        this.setDescription(description);
        this.setA(a);
        this.setB(b);
        this.setC(c);
        this.setD(d);
        this.setAnswer(answer);
    }



    public String getQuestno() {
        return questno;
    }

    public void setQuestno(String questno) {
        this.questno = questno;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
